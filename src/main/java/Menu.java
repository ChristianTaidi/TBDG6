public class Menu {
    private QueryExecutioner queryExecutioner;
    private int number;

    public Menu() {
        this.queryExecutioner = new QueryExecutioner();
        number= 0;
    }

    public void imprimirMenu() {
        System.out.println("---------------------------------------------------------");   
        System.out.println("0.- Exit");
        System.out.println("1.- Show a list of comics where a selected character appears.");
        System.out.println("2.- Show a list of descending frequencies of every eye color.");
        System.out.println("3.- Show the comic with the highest number of characters.");
        System.out.println("4.- Show the publisher with the highest number of bald characters.");
        System.out.println("5.- Show the ratio between men and women in a concrete universe.");
        System.out.println("6.- Show the most hated character: The oldest one, being dead and with the least appearances.");
        System.out.println("7.- Show which universe has more classic comics (the ones that have characters existing before the year 2000), Marvel or DC Comics.");
        System.out.println("8.- Show who has a higher average intelligence, humans or cyborgs.");
        System.out.println("9.- Show who is the first character to appear with superpowers in a comic.");
        System.out.println("10.- Question TBD.");
        System.out.println("11.- Dump Data to Mongo");
        System.out.println("---------------------------------------------------------");
        System.out.println("Introduce the number corresponding to your desired query:");
    }

    public void selectQuery(){
        
            int option = -1;
            String displayMenu = "";
            int iteration = 0;
            boolean shouldEnd = false;
            MenuScanner menuScanner = new MenuScanner();
            do {
                if(iteration>0){
                    displayMenu = menuScanner.readContinue();
                    
                    if(displayMenu.equals("n")){
                        shouldEnd = true;
                        option = 0;
                    }
                }
                if(!shouldEnd){
                    imprimirMenu();
                        option = menuScanner.readInt();
                    switch (option) {
                        case 0:
                            System.out.println("0.- Exit");
                            iteration++;
                            break;
                        case 1:
                            System.out.println("1.- Show a list of comics where a selected character appears.");
                            System.out.println(" -- Enter the name of the character to look for");
                            String charName = menuScanner.readString();
                            this.queryExecutioner.executeListOfComicsOfACharacter(charName);
                            iteration++;
                            break;
                        case 2:
                            System.out.println("2.- Show a list of descending frequencies of every eye color.");
                            this.queryExecutioner.executeEyeColorFrequencies();
                            iteration++;
                            break;
                        case 3:
                            System.out.println("3.- Show the comic with the highest number of characters.");
                            this.queryExecutioner.executeComicWithHighestNumberOfCharacters();
                            iteration++;
                            break;
                        case 4:
                            System.out.println("4.- Show the publisher with the highest number of bald characters.");
                            this.queryExecutioner.executePublisherWithMostBaldCharacters();
                            iteration++;
                            break;
                        case 5:
                            System.out.println("5.- Show the ratio between men and women in a concrete universe.");
                            System.out.println(" -- Enter 1 to choose Marvel as desired universe or other number for DC Comics -- ");
                            int universe = menuScanner.readInt();
                            this.queryExecutioner.executeMenAndWomenRatioInAConcreteUniverse(universe);
                            iteration++;
                            break;
                        case 6:
                            System.out.println("6.- Show the most hated character: The oldest one, being dead and with the least appearances.");
                            this.queryExecutioner.executeMostHatedCharacter();
                            iteration++;
                            break;
                        case 7:
                            System.out.println("7.- Show which universe has more classic comics (the ones that have characters existing before the year 2000), Marvel or DC Comics.");
                            this.queryExecutioner.executeUniverseWithMostClassicComics();
                            iteration++;
                            break;
                        case 8:
                            System.out.println("8.- Show who has a higher average intelligence.");
                            this.queryExecutioner.executeAllRaces();
                            System.out.println("Introduzca una raza");
                            String race1 = menuScanner.readString();
                            String race2;
                            do {
                                System.out.println("Introduzca otra raza");
                                race2 = menuScanner.readString();
                            }while (race1.equals(race2));
                            this.queryExecutioner.executeAverageIntelligence(race1,race2);
                            iteration++;
                            break;
                        case 9:
                            System.out.println("9.- Show who is the first character to appear with superpowers in a comic.");
                            this.queryExecutioner.executeFirstCharacterWithSuperpowers();
                            iteration++;
                            break;
                        case 10:
                            System.out.println("10.- Characters who appear in a saga (CapitanAmérica) .");
                            this.queryExecutioner.executeCharactersInSaga();
                            iteration++;
                            break;
                        case 11:
                            System.out.println(" -- Starting Data Dump to MongoDB -- ");
                            queryExecutioner.dumpData();
                            break;
                        default:
                            System.out.println("No query has been selected");
                        }
                    }
           }while (option != 0) ;
        // }catch (InputMismatchException e) {
        //     System.out.println("Please introduce a number next time");
        // }
    }
}