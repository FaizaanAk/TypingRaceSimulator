public class test {
    public static void main(String[] args) 
    {
        //Test 1: That progress cannot go below zero after calling slideBack()
        System.out.println("Testing that progress cannot go below zero after calling slideBack()");
        Typist t1 = new Typist('A', "Typist1", 0.85);
        t1.typeCharacter();
        t1.typeCharacter();

        System.out.println(t1.getProgress());
        t1.slideBack(5);
        System.out.println(t1.getProgress());
        t1.slideBack(1);
        System.out.println(t1.getProgress());

        System.out.println();

        //Test 2:That burnout correctly counts down turn by turn and clears at zero
        System.out.println("Testing that burnout correctly counts down turn by turn and clears at zero");
        Typist t2 = new Typist('B', "Typist2", 0.60);

        t2.burnOut(3);
        System.out.println("Burnt out is " + t2.isBurntOut() + " and " + t2.getBurnoutTurnsRemaining() + " turns remaining ");

        t2.recoverFromBurnout();
        System.out.println("Burnt out is " + t2.isBurntOut() + " and " + t2.getBurnoutTurnsRemaining() + " turns remaining ");

        t2.recoverFromBurnout();
        System.out.println("Burnt out is " + t2.isBurntOut() + " and " + t2.getBurnoutTurnsRemaining() + " turns remaining ");

        t2.recoverFromBurnout();
        System.out.println("Burnt out is " + t2.isBurntOut() + " and " + t2.getBurnoutTurnsRemaining() + " turns remaining ");

        System.out.println();

        //Test 3: That resetToStart() clears both progress and burnout state
        System.out.println("Testing that resetToStart() clears both progress and burnout state");
        Typist t3 = new Typist('C', "Typist 3", 0.70);
        
        t3.typeCharacter();
        t3.typeCharacter(); 
        t3.typeCharacter();
        t3.burnOut(5);

        System.out.println("Current progress: " + t3.getProgress() + ", Burnt out is " + t3.isBurntOut() + " and Burnout turns remaining: " + t3.getBurnoutTurnsRemaining());
        t3.resetToStart();
        System.out.println("After reset: Progress: " + t3.getProgress() + ", Burnt out is " + t3.isBurntOut() + " and Burnout turns remaining: " + t3.getBurnoutTurnsRemaining());

        System.out.println();

        //Test 4: That accuracy cannot be set outside the 0.0–1.0 range
        System.out.println("Testing that accuracy cannot be set outside the 0.0 to 1.0 range");
        Typist t4 = new Typist('D', "Typist 4", 0.50);

        t4.setAccuracy(1.5);
        System.out.println("Accuracy: " + t4.getAccuracy());

        t4.setAccuracy(-0.5);
        System.out.println("Accuracy: " + t4.getAccuracy());

        t4.setAccuracy(0.75);
        System.out.println("Accuracy: " + t4.getAccuracy());

        System.out.println();

        //Test 5: Normal forward movement via typeCharacter()
        System.out.println("Testing normal forward movement via typeCharacter()");
        Typist t5 = new Typist('E', "Typist 5", 0.90);

        System.out.println("Start progress: " + t5.getProgress());

        t5.typeCharacter();
        System.out.println("First character typed: " + t5.getProgress());

        t5.typeCharacter();
        System.out.println("Second character typed: " + t5.getProgress());

        t5.typeCharacter();
        System.out.println("Third character typed: " + t5.getProgress());

        System.out.println();
    }
}   

