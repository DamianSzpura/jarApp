package jarHandling;

import javassist.CtConstructor;

public class JarConstructor extends JarClassAttribute {

  private CtConstructor classConstructor;
  private String name;

  public JarConstructor(CtConstructor constructor) {
    this.classConstructor = constructor;
    this.name = constructor.getLongName();
  }

  public CtConstructor getClassConstructor() {
    return classConstructor;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
