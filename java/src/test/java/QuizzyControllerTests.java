import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class QuizzyControllerTests {
    private QuizzyController quizzy;
    private String existingQuiz;
    private String mathias;
    private String eric;
    private String martin;

    @BeforeEach
    public void Setup() {
        quizzy = new QuizzyController();
        existingQuiz = "star wars";

        mathias = "Mathias";
        eric = "Eric";
        martin = "Martin";
    }

    @Test
    public void EricAnswersMostCorrectly_EricIsWinner() {
        final UUID gameId = quizzy.open(existingQuiz);

        assertEquals("Open", quizzy.status(gameId));

        quizzy.join(gameId, mathias);
        quizzy.join(gameId, eric);
        quizzy.join(gameId, martin);
        quizzy.startGame(gameId);

        assertEquals("Started", quizzy.status(gameId));

        quizzy.answer(gameId, mathias, "incorrect answer", 10);
        quizzy.answer(gameId, eric, "answer1", 10);
        quizzy.answer(gameId, martin, "answer1", 10);
        assertEquals("question2", quizzy.question(gameId));

        quizzy.answer(gameId, martin, "incorrect answer", 10);
        quizzy.answer(gameId, mathias, "incorrect answer", 10);
        quizzy.answer(gameId, eric, "answer2", 10);

        assertEquals("Finished", quizzy.status(gameId));

        assertEquals("Eric", quizzy.winner(gameId));
    }

    @Test
    public void EricAnswersFastests_EricIsWinner() {
        UUID gameId = quizzy.open(existingQuiz);

        quizzy.join(gameId, mathias);
        quizzy.join(gameId, eric);
        quizzy.join(gameId, martin);
        quizzy.startGame(gameId);


        String question = quizzy.question(gameId);
        quizzy.answer(gameId, mathias, "answer1", 10);
        quizzy.answer(gameId, eric, "answer1", 5);
        quizzy.answer(gameId, martin, "answer1", 10);

        quizzy.answer(gameId, martin, "answer2", 10);
        quizzy.answer(gameId, mathias, "answer2", 10);
        quizzy.answer(gameId, eric, "answer2", 0);

        assertEquals("Eric", quizzy.winner(gameId));
    }

    @Test
    public void MultiplePlayersWithCorrectAnswers_And_SameTimes_ButEricIsFasterOnTheLastQuestion_EricIsWinner() {
        UUID gameId = quizzy.open(existingQuiz);

        quizzy.join(gameId, mathias);
        quizzy.join(gameId, eric);
        quizzy.join(gameId, martin);
        quizzy.startGame(gameId);


        String question = quizzy.question(gameId);
        quizzy.answer(gameId, mathias, "answer1", 5);
        quizzy.answer(gameId, eric, "answer1", 10);
        quizzy.answer(gameId, martin, "answer1", 10);

        quizzy.answer(gameId, martin, "answer2", 10);
        quizzy.answer(gameId, mathias, "answer2", 10);
        quizzy.answer(gameId, eric, "answer2", 5);

        assertEquals("Eric", quizzy.winner(gameId));
    }

    @Test
    public void StartAQuiz_3PlayersJoin_EricAnswersAllCorrectlyAndFastest_EricIsWinner() {
        UUID gameId = quizzy.open(existingQuiz);

        assertEquals("Open", quizzy.status(gameId));
        assertEquals(existingQuiz, quizzy.games().toArray()[0]);

        quizzy.join(gameId, mathias);
        quizzy.join(gameId, eric);
        quizzy.join(gameId, martin);
        quizzy.startGame(gameId);

        assertEquals("Started", quizzy.status(gameId));

        String question = quizzy.question(gameId);
        quizzy.answer(gameId, mathias, "answer1", 10);
        quizzy.answer(gameId, eric, "answer1", 10);
        assertEquals("question1", quizzy.question(gameId));
        quizzy.answer(gameId, martin, "answer1", 10);
        assertEquals("question2", quizzy.question(gameId));

        quizzy.answer(gameId, martin, "answer2", 10);
        quizzy.answer(gameId, mathias, "incorrect answer", 10);
        quizzy.answer(gameId, eric, "answer2", 5);

        assertEquals("Finished", quizzy.status(gameId));

        assertEquals("Eric", quizzy.winner(gameId));
    }

    @Test
    public void Open_Existing_Quiz() {
        UUID gameId = quizzy.open(existingQuiz);
        assertEquals("Open", quizzy.status(gameId));
    }

    @Test
    public void Open_NonExisting_Quiz() {
        assertThrows(IllegalArgumentException.class, () -> quizzy.open("non existing quiz"));
    }

    @Test
    public void StartingAnExistingGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, mathias);
        quizzy.startGame(gameId);
        assertEquals("Started", quizzy.status(gameId));
    }

    @Test
    public void StartingANonExistingGame() {
        assertThrows(IllegalArgumentException.class, () -> quizzy.startGame(UUID.randomUUID()));
    }

    @Test
    public void ExistingPlayer_Joins_OpenGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, mathias);

        assertEquals("Open", quizzy.status(gameId));
    }

    @Test
    public void NonExistingPlayer_Joins_AnExistingGame() {
        UUID gameId = quizzy.open(existingQuiz);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.join(gameId, "Non existing player"));
        assertEquals("Player does not exist", exception.getMessage());
    }

    @Test
    public void ExistingPlayer_Joins_NonExistingGame() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.join(UUID.randomUUID(), mathias));
        assertEquals("Game does not exist", exception.getMessage());
    }

    @Test
    public void ExistingPlayer_Joins_StartedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, eric);

        quizzy.startGame(gameId);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.join(gameId, mathias));
        assertEquals("Game already started", exception.getMessage());
    }

    @Test
    public void ExistingPlayer_Joins_ExistingGame_Twice() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, mathias);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.join(gameId, mathias));
        assertEquals("Player joined already", exception.getMessage());
    }

    @Test
    public void ExistingPlayer_Joins_FinishedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.startGame(gameId);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.join(gameId, mathias));
        assertEquals("Game is finished", exception.getMessage());
    }

    @Test
    public void ExistingGame_NoPlayerJoins_StartGame_StatusIs() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.startGame(gameId);
        assertEquals("Finished", quizzy.status(gameId));
    }

    @Test
    public void Question_On_NonExistingGame() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.question(UUID.randomUUID()));
        assertEquals("Game does not exist", exception.getMessage());
    }

    @Test
    public void Question_On_NotStartedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.question(gameId));
        assertEquals("Game is not started", exception.getMessage());
    }

    @Test
    public void Question_On_FinishedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.startGame(gameId);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.question(gameId));
        assertEquals("Game is finished", exception.getMessage());
    }

    @Test
    public void Question_On_StartedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, eric);
        quizzy.startGame(gameId);

        assertEquals("question1", quizzy.question(gameId));
    }


    @Test
    public void AnswerQuestion_On_NonExistingGame() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.answer(UUID.randomUUID(), martin, "", 0));
        assertEquals("Game does not exist", exception.getMessage());
    }

    @Test
    public void AnswerQuestion_On_NotStartedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.answer(gameId, martin, "", 0));
        assertEquals("Game is not started", exception.getMessage());
    }

    @Test
    public void AnswerQuestion_On_FinishedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.startGame(gameId);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.answer(gameId, martin, "", 0));
        assertEquals("Game is finished", exception.getMessage());
    }

    @Test
    public void StartedGame_AnswerQuestionTwice() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, mathias);
        quizzy.join(gameId, eric);

        quizzy.startGame(gameId);
        quizzy.answer(gameId, mathias, "", 0);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.answer(gameId, mathias, "", 0));
        assertEquals("question already answered", exception.getMessage());
    }

    @Test
    public void StartedGame_NonJoinedPlayer_AnswersQuestion() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, mathias);

        quizzy.startGame(gameId);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.answer(gameId, martin, "", 0));
        assertEquals("Player has not joined", exception.getMessage());
    }

    @Test
    public void StartedGame_AnswerAllQuestions() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, mathias);
        quizzy.join(gameId, eric);

        quizzy.startGame(gameId);
        assertEquals("question1", quizzy.question(gameId));
        quizzy.answer(gameId, mathias, "", 0);
        quizzy.answer(gameId, eric, "", 0);

        assertEquals("question2", quizzy.question(gameId));
        quizzy.answer(gameId, eric, "", 0);
        quizzy.answer(gameId, mathias, "", 0);

        assertEquals("Finished", quizzy.status(gameId));
    }

    @Test
    public void Status_On_NonExistingGame() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.status(UUID.randomUUID()));
        assertEquals("Game does not exist", exception.getMessage());
    }

    @Test
    public void Winner_On_NonExistingGame() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.winner(UUID.randomUUID()));
        assertEquals("Game does not exist", exception.getMessage());
    }

    @Test
    public void Winner_On_NotStartedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.winner(gameId));
        assertEquals("Game is not started", exception.getMessage());
    }

    @Test
    public void Winner_On_StartedGame() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.join(gameId, eric);
        quizzy.startGame(gameId);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.winner(gameId));
        assertEquals("Game is started", exception.getMessage());
    }

    @Test
    public void Winner_On_FinishedGame_WithoutPlayers() {
        UUID gameId = quizzy.open(existingQuiz);
        quizzy.startGame(gameId);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> quizzy.winner(gameId));
        assertEquals("Game is finished but no players", exception.getMessage());
    }
}
