# change-method-signature
This tool will detect all java method signature changes in git repositories and reports commits which have added a new parameter to method signature.

# Building from the source code

This project uses maven as the build system. You need to have maven installed on your machine to be abale to build the source. In order to build the artifact from the source, clone the repository and run the following command inside the cloned repository directory: 

  `mvn clean package`
  
This will create a directory named: *"target"*, inside which is the built artifact
named: *"detect-refactorings-jar-with-dependencies.jar"*

This jar artifact includes everything you need to run the tool in command line.

# Usage

To run the tool from the command line, switch to *"target"* directory and run the following
command in command line:

    `java -jar detect-refactorings-jar-with-dependencies.jar ${repository_url}`
    
> Note that *"${repository_url}"* is the remote address of the target repository that you want to analyse.

> `Ex: java -jar detect-refactorings-jar-with-dependencies.jar https://github.com/junit-team/junit4.git`

