package Assig1;

class RegularExpression {
    String name;
    String regex;

    public RegularExpression(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    @Override
    public String toString() {
        return name + ": " + regex;
    }
}