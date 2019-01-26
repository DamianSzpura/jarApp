package jarHandling;

import javassist.CtField;
import javassist.NotFoundException;

public class JarField extends JarClassAttribute {

  private CtField classField;
  private String name;

  public JarField(CtField field) {
    this.classField = field;
    try {
      this.name = field.getType().getName()+" "+field.getName();
    } catch (NotFoundException e) {
      this.name = field.getName();
    }
  }

  public CtField getClassField() {
    return classField;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
