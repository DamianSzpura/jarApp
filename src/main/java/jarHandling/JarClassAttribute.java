package jarHandling;

import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

public abstract class JarClassAttribute {

    private CtField classField;
    private CtMethod classMethod;
    private CtConstructor classConstructor;
    private String name;

    public CtMethod getClassMethod() {
        return classMethod;
    }

    public CtConstructor getClassConstructor() {
        return classConstructor;
    }

    public CtField getClassField() {
        return classField;
    }

    public String getName() {
        return name;
    }
}
