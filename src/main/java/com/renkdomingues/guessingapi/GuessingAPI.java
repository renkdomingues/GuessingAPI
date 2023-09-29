package com.renkdomingues.guessingapi;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

import static java.lang.Character.toLowerCase;
import static java.lang.System.*;
import static java.util.stream.Collectors.joining;
import static org.eu.zajc.akiwrapper.Akiwrapper.Answer.*;
import static org.eu.zajc.akiwrapper.core.entities.Server.GuessType.CHARACTER;
import static org.eu.zajc.akiwrapper.core.entities.Server.Language.ENGLISH;
import static org.eu.zajc.akiwrapper.core.entities.Server.Language.PORTUGUESE;

import java.util.*;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.*;
import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.entities.Server.*;
import org.eu.zajc.akiwrapper.core.exceptions.ServerNotFoundException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SuppressWarnings("javadoc")

@RestController
@RequestMapping("/api")
public class GuessingAPI {

    private static final String ANSWER_TIP =
            "Y or yes, N or no, DK or don't know, P or probably, PN or probably not, or go back one step with B or back.";
    private static final Scanner IN = new Scanner(System.in).useDelimiter("\n");

    private static Akiwrapper aw;
    private static Question question;
    private static Boolean isDescription;
    private static Guess guess;

    public GuessingAPI(){

        boolean filterProfanity = getProfanityFilter();
        // Gets player's age. Like the Akinator's website, this will turn on the profanity
        // filter if the age entered is below 16.

        var language = getLanguage();
        // Gets player's language. Akinator will give the user localized questions and
        // guesses depending on user's language.

        var guessType = getGuessType();
        // Gets the guess type.

        try {
            isDescription = false;
            question = null;
            guess = null;

            aw = new AkiwrapperBuilder().setFilterProfanity(filterProfanity)
                    .setLanguage(language)
                    .setGuessType(guessType)
                    .build();
        } catch (ServerNotFoundException e) {
            err.println("Unsupported combination of language and guess type");
            return;
        }
        // Builds the Akiwrapper instance, this is what we'll be using to perform
        // operations such as answering questions, fetching guesses, etc

        //while (aw.getQuestion() != null) {
            // Runs while there are still questions left

            //question = aw.getQuestion();
            //if (question == null)
                //break;
            // Breaks the loop if question is null; /should/ not occur, but safety is still
            // first

            //out.printf("Question #%d%n\t%s%n", question.getStep() + 1, question.getQuestion());
            // Displays the question.

            //if (question.getStep() == 0)
                //out.printf("%nAnswer with %s%n", ANSWER_TIP);
            // Displays the tip (only for the first time)

            //out.print("> ");

            //answerQuestion(aw);
            // Displays the question and prompts the player for an answer

            //reviewSuggestedGuess(aw);
            // Checks if any guess is available and prompts the player
        //}

        //reviewSuggestedGuess(aw);
        // Reviews the final guess

        //finish(false);
        // Loses if all guesses are rejected.
    }

    @GetMapping("/guess")
    @ResponseBody
    public String Guess(){
        question = aw.getQuestion();
        if (question == null)
            return "Não há perguntas disponíveis!";

        int stepCount = question.getStep() + 1;

        return stepCount + ": " + question.getQuestion();
    }

    @PostMapping("/answer")
    @ResponseBody
    public String Answer(@RequestBody String response){

        if(question == null)
            return "É necessário primeiramente iniciar as perguntas /guess";

        JSONObject jsonResponse = new JSONObject(response.toString());
        // Obter o valor da chave "response"
        response = jsonResponse.getString("response");

        if(isDescription && response.equalsIgnoreCase("y")){
            aw.confirmGuess(guess);
            return finish(true);
        }

        guess = aw.suggestGuess();

        String retorno = answerQuestion(aw, response);
        if(retorno != "")
            return retorno;

        if (guess != null) {
            if (guess.getDescription() != null) {
                isDescription = true;
                return guess.getName() + guess.getDescription() + ". É isso que estava pensado?";
            } else {
                isDescription = false;
                aw.rejectLastGuess();
            }
        }

        return Guess();
    }

    private static Guess reviewSuggestedGuess(@Nonnull Akiwrapper aw) {
        var guess = aw.suggestGuess();
        if (guess != null) {
            if (reviewGuess(guess)) {
                aw.confirmGuess(guess);
                finish(true);
                exit(0);
            } else {
                aw.rejectLastGuess();
            }
        }
        return guess;
    }

    private static boolean reviewGuess(@Nonnull Guess guess) {
        out.println(guess.getName());
        out.print("\t");
        if (guess.getDescription() == null)
            out.print("(no description)");
        else
            out.print(guess.getDescription());
        out.println();
        // Displays the guess' information

        out.print("É isso que estava pensando?");
        var input = IN.next().trim();
        // Asks the player if the guess is correct

        return input.isEmpty() || toLowerCase(input.charAt(0)) == 'y';
    }

    private static String answerQuestion(@Nonnull Akiwrapper aw, String response) {
            // Prompts the player for an answer

            String retorno = "";

            switch (response) {
                case "y":
                case "yes":
                    aw.answer(YES);
                    break ;

                case "n":
                case "no":
                    aw.answer(NO);
                    break;

                case "dk":
                case "don'tknow":
                case "dontknow":
                case "dont know":
                case "don't know":
                    aw.answer(DONT_KNOW);
                    break;

                case "p":
                case "probably":
                    aw.answer(PROBABLY);
                    break;

                case "pn":
                case "probablynot":
                case "probably not":
                    aw.answer(PROBABLY_NOT);
                    break;

                /*case "b":
                case "back":
                    if (aw.getStep() == 0)
                        out.println("Can't undo on the first question.");
                    else
                        aw.undoAnswer();
                    break;

                case "debug":
                    displayDebug(aw);
                    break;*/
                // Displays some debug information.

                default:
                    retorno = "Please answer with either " + ANSWER_TIP;
                    break;


            /*var input = IN.next().toLowerCase();

            switch (input) {
                case "y":
                case "yes":
                    aw.answer(YES);
                    break main;

                case "n":
                case "no":
                    aw.answer(NO);
                    break main;

                case "dk":
                case "don'tknow":
                case "dontknow":
                case "dont know":
                case "don't know":
                    aw.answer(DONT_KNOW);
                    break main;

                case "p":
                case "probably":
                    aw.answer(PROBABLY);
                    break main;

                case "pn":
                case "probablynot":
                case "probably not":
                    aw.answer(PROBABLY_NOT);
                    break main;

                case "b":
                case "back":
                    if (aw.getStep() == 0)
                        out.println("Can't undo on the first question.");
                    else
                        aw.undoAnswer();
                    break main;

                case "debug":
                    displayDebug(aw);
                    break;
                // Displays some debug information.

                default:
                    out.println("Please answer with either " + ANSWER_TIP);
                    break;
            }*/

        }

        return retorno;
    }

    private static void displayDebug(Akiwrapper aw) {
        var question = aw.getQuestion();
        out.println("Debug information:");
        out.printf("\tCurrent API server: %s%n", aw.getServer().getUrl());
        out.printf("\tProgression: %f%%%n", question == null ? -1 : question.getProgression());
        out.println("\tGuesses:");
        aw.getGuesses().stream().forEach(g -> out.printf("\t\t%f - %s%n", g.getProbability(), g.getName()));
        out.print("> ");
    }

    private static String finish(boolean win) {
        if (win) {
            // If Akinator has won.
            //out.println("Great!");
            //out.println("\tGuessed right one more time. I love playing with you!");
            return "Boa!\tPergunte mais uma vez. Adoro jogar com você!";
        } else {
            // If the player has won.
            //out.println("Bravo!");
            //out.println("\tYou have defeated me.");
            return "Nossa!\tVocê me venceu, muito bem!";
        }
    }

    private static boolean getProfanityFilter() {
        //out.print("Enable profanity filtering? [y/N] ");
        //var input = IN.next().trim();
        //return !input.isEmpty() && toLowerCase(input.charAt(0)) == 'y';
        return false;
    }

    @Nonnull
    @SuppressWarnings("null")
    private static Language getLanguage() {
		/*var languages = EnumSet.allOf(Language.class);

		out.print("What's your language? [English] ");
		while (true) {
			var input = IN.next().trim().toUpperCase();

			if (input.isEmpty())
				return ENGLISH;

			var language = languages.stream().filter(l -> l.toString().equals(input)).findAny();

			if (language.isPresent()) {
				return language.orElseThrow();

			} else {
				out.println(languages.stream()
					.map(Enum::toString)
					.collect(joining("\n-", "Sorry, that language isn't supported. Available options:\n-", "")));
			}
		}*/

        return PORTUGUESE;
    }

    @Nonnull
    @SuppressWarnings("null")
    private static GuessType getGuessType() {
		/*var guessTypes = EnumSet.allOf(GuessType.class);

		out.print("What will you be guessing? [character] ");
		while (true) {
			var input = IN.next().trim().toUpperCase();

			if (input.isEmpty())
				return CHARACTER;

			var guessType = guessTypes.stream().filter(l -> l.toString().equals(input)).findAny();

			if (guessType.isPresent()) {
				return guessType.orElseThrow();

			} else {
				out.println("" + guessTypes.stream()
					.map(Enum::toString)
					.collect(joining("\n-", "Sorry, that guess type isn't supported. Choose between\n-", "")));
			}
		}*/

        return CHARACTER;
    }
}
