package org.azodi.prj;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import refdiff.core.RefDiff;
import refdiff.core.api.GitService;
import refdiff.core.api.RefactoringType;
import refdiff.core.rm2.model.refactoring.SDChangeMethodSignature;
import refdiff.core.rm2.model.refactoring.SDRefactoring;
import refdiff.core.util.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Runner {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("Welcome...");
        System.out.println("-------------------------------------------------");
        System.out.println("This tool will analyse all the commits");
        System.out.println("for a java repository and finds those commits");
        System.out.println("that have added a parameter to an existing method.");
        System.out.println("");
        System.out.println("-------------------------------------------------");
        System.out.println("This tool will then report those commits in a");
        System.out.println("CSV file as its output");
        System.out.println("");
        System.out.println("");
        System.out.println("");

        String repoCloneUrl;
        if (args != null && args.length > 0) {
            repoCloneUrl = args[0];
        } else {
            System.out.println("You should pass repository url to use this tool");

            throw new IllegalArgumentException();
        }

        System.out.println("Cloning the repository...");
        System.out.println("");

        RefDiff refDiff = new RefDiff();
        GitService gitService = new GitServiceImpl();
        String clonePath = getTemporaryClonePathLocation();
        String repoClone = clonePath + File.separator + getRandomCloneName();

        try (Repository repository = gitService.cloneIfNotExists(repoClone, repoCloneUrl)) {

            System.out.println("Cloned repository: " + "\"" + repoCloneUrl + "\"" + " To " + "\"" + repoClone + "\"");
            System.out.println("");

            System.out.println("Analysing the commits...");
            System.out.println("");

            Git git = new Git(repository);
            Iterable<RevCommit> commits = git.log().all().call();

            HEADERS[] values = HEADERS.values();
            List<String> headers = new ArrayList<>();
            for (HEADERS value : values) {
                headers.add(value.Title());
            }

            String reportName = getRandomOutputName();
            CSVOutput output = new CSVOutput(reportName, headers);

            for (RevCommit commit : commits) {
                List<SDRefactoring> refactorings = refDiff.detectAtCommit(repository, commit.getName());
                if (refactorings != null && !refactorings.isEmpty()) {
                    for (SDRefactoring refactoring : refactorings) {
                        if (refactoring.getRefactoringType() == RefactoringType.CHANGE_METHOD_SIGNATURE) {
                            SDChangeMethodSignature ref = (SDChangeMethodSignature) refactoring;
                            if (ref.getMethodBefore().parameterCount() < ref.getMethodAfter().parameterCount()) {
                                String fileName = ref.getMethodBefore().key().toString().split("#")[0];
                                String commitSHA = commit.getName();
                                String oldMethodSignature = ref.getMethodBefore().getVerboseSimpleName();
                                String newMethodSignature = ref.getMethodAfter().getVerboseSimpleName();

                                output.addItem(Arrays.asList(
                                        commitSHA,
                                        fileName,
                                        oldMethodSignature,
                                        newMethodSignature
                                ));
                            }
                        }
                    }
                }
            }

            output.close();

            System.out.println("Analysed the repo...");
            System.out.println("Checkout output report: " + "\"" + reportName + "\"");
            System.out.println("");

        } catch (Exception e) {
            throw new RuntimeException("Couldn't analyse the repository", e);
        } finally {
            try {
                Files.delete(Paths.get(repoClone));
            } catch (IOException ignored) {
            }
        }
    }

    private static String getTemporaryClonePathLocation() {
        try {
            File test = File.createTempFile("test", ".tmp");
            return test.getAbsolutePath().substring(0, test.getAbsolutePath().lastIndexOf(File.separator));
        } catch (IOException e) {
            throw new RuntimeException("Could not clone repository", e);
        }
    }

    private static String getRandomCloneName() {
        SecureRandom random = new SecureRandom();
        return "clone" + String.valueOf(random.nextInt(100000));
    }

    private static String getRandomOutputName() {
        SecureRandom random = new SecureRandom();
        return "report" + String.valueOf(random.nextInt(100000)) + ".csv";
    }


    public enum HEADERS {
        COMMIT_SHA("Commit SHA"),
        JAVA_FILE("Java File"),
        OLD_SIGNATURE("Old Function Signature"),
        NEW_SIGNATURE("New Function Signature"),;

        private String title;

        HEADERS(String title) {
            this.title = title;
        }

        public String Title() {
            return title;
        }
    }
}
