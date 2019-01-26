package jarHandling;

import javassist.CtMethod;
import javassist.NotFoundException;

public class JarMethod extends JarClassAttribute {

  private CtMethod classMethod;
  private String name;

  public JarMethod(CtMethod method) {
    this.classMethod = method;
    try {
      this.name = method.getReturnType().getName() + " "+ method.getName();
    } catch (NotFoundException e) {
      this.name = method.getName();
    }
  }

  public CtMethod getClassMethod() {
    return classMethod;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
