package refdiff.core.rm2.model.refactoring;

import refdiff.core.api.RefactoringType;
import refdiff.core.rm2.model.SDMethod;

public class SDChangeMethodSignature extends SDRefactoring {

    private final SDMethod methodBefore;
    private final SDMethod methodAfter;
    
    public SDChangeMethodSignature(SDMethod methodBefore, SDMethod methodAfter) {
        super(RefactoringType.CHANGE_METHOD_SIGNATURE, methodBefore, methodBefore, methodAfter);
        this.methodBefore = methodBefore;
        this.methodAfter = methodAfter;
    }

    public SDMethod getMethodBefore() {
        return methodBefore;
    }

    public SDMethod getMethodAfter() {
        return methodAfter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(' ');
        sb.append(methodBefore.getVerboseSimpleName());
        sb.append(" renamed to ");
        sb.append(methodAfter.getVerboseSimpleName());
        sb.append(" in class ");
        sb.append(methodAfter.container().fullName());
        return sb.toString();
    }
}
