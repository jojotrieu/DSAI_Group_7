public class Main {

    public static void main(String[] args) {

        //KeyboardParser parser = new KeyboardParser("/Users/nicograssetto/IdeaProjects/Playground/src/qwerty.txt");
        //parser.getKeyboard();
        QuestionHandler questionHandler = new QuestionHandler("/Users/nicograssetto/IdeaProjects/Playground/src/QueryDatabase.txt", "/Users/nicograssetto/IdeaProjects/Playground/src/qwerty.txt");
        System.out.println("Output:" + questionHandler.answer("Who is Pietro", 0.3, 0.5));
        System.out.println("==============================");
        System.out.println("Output:" + questionHandler.answer("What is Pietro", 1.0, 0.5));
        System.out.println("==============================");
        System.out.println("Output:" + questionHandler.answer("What is the weather in Maastricht today", 0.3, 0.5));

    }

}
