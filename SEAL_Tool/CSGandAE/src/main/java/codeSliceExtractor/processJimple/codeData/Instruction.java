package codeSliceExtractor.processJimple.codeData;

/**
 * An Instruction can represent a parameter, a function, a constant, an operation, an if and others
 * (As you can see in Tag Class)
 */
public class Instruction {
    private Tag tag;
    private String name;

    public Instruction(Tag tag) {
        this.tag = tag;
        this.name = "";
    }

    public Instruction(Tag tag, String name) {
        this.tag = tag;
        this.name = name;
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getName() {
        return this.name;
    }

    public void print() {
        System.out.println("Instruction tag: " + this.getTag());
        if (this.getTag() == Tag.FUNCTION) {
            System.out.println("Function Name: " + this.getName());
        }
        else if(this.getTag() == Tag.PARAMETER) {
            System.out.println("Parameter Name: " + this.getName());
        }
        else if(this.getTag() == Tag.SOURCE) {
            System.out.println("Source Name: " + this.getName());
        }
        else if(this.getTag() == Tag.ARRAY) {
            System.out.println("Array Name: " + this.getName());
        }
        else if(this.getTag() == Tag.OBJECT) {
            System.out.println("Object Name: " + this.getName());
        }
    }

    @Override
    public boolean equals(Object object) {
        if(object != null) {
            if (object instanceof Instruction) {
                return ((Instruction) object).getTag().equals(this.tag) && ((Instruction) object).getName().equals(this.name);
            }
        }
        return false;
    }

}
