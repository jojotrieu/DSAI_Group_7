public class Test {
    public static void main(String[] args) {
        SkillParser parser = new SkillParser();
        parser.splitSkill("Question where is <person> at <time>");
        parser.makeSkill("Question where is <person> at <time>");

    }
}
