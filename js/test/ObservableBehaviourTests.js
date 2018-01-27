const uuid4 = require('uuid/v4');
const tap = require('tap');
const Quiz = require('../src/QuizzyController').Quiz;
const QuizzyController = require('../src/QuizzyController').QuizzyController;

let _quizzy;
let _existingQuiz;
let _mathias;
let _eric;
let _martin;

function Setup()
{
    _quizzy = new QuizzyController();
    _existingQuiz = "star wars";

    _mathias = "Mathias";
    _eric = "Eric";
    _martin = "Martin";
}

tap.test("EricAnswersMostCorrectly_EricIsWinner", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    
    test.equal("Open", _quizzy.Status(gameId));

    test.equal([_existingQuiz].length, _quizzy.Games().length);
    test.equal([_existingQuiz][0], _quizzy.Games()[0]);

    _quizzy.Join(gameId, _mathias);
    _quizzy.Join(gameId, _eric);
    _quizzy.Join(gameId, _martin);
    _quizzy.StartGame(gameId);
    
    test.equal("Started", _quizzy.Status(gameId));

    _quizzy.Answer(gameId, _mathias, "incorrect answer", 10);
    _quizzy.Answer(gameId, _eric, "answer1", 10);
    _quizzy.Answer(gameId, _martin, "answer1", 10);
    test.equal("question2", _quizzy.Question(gameId));
    
    _quizzy.Answer(gameId, _martin, "incorrect answer", 10);
    _quizzy.Answer(gameId, _mathias, "incorrect answer", 10);
    _quizzy.Answer(gameId, _eric, "answer2", 10);
    
    test.equal("Finished", _quizzy.Status(gameId));
    
    test.equal("Eric", _quizzy.Winner(gameId));
    test.end();
})


tap.test("EricAnswersFastests_EricIsWinner", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    
    _quizzy.Join(gameId, _mathias);
    _quizzy.Join(gameId, _eric);
    _quizzy.Join(gameId, _martin);
    _quizzy.StartGame(gameId);
    

    var question = _quizzy.Question(gameId);
    _quizzy.Answer(gameId, _mathias, "answer1", 10);
    _quizzy.Answer(gameId, _eric, "answer1", 5);
    _quizzy.Answer(gameId, _martin, "answer1", 10);
    
    _quizzy.Answer(gameId, _martin, "answer2", 10);
    _quizzy.Answer(gameId, _mathias, "answer2", 10);
    _quizzy.Answer(gameId, _eric, "answer2", 0);
    
    test.equal("Eric", _quizzy.Winner(gameId));
    test.end();
})


tap.test("MultiplePlayersWithCorrectAnswers_And_SameTimes_ButEricIsFasterOnTheLastQuestion_EricIsWinner", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    
    _quizzy.Join(gameId, _mathias);
    _quizzy.Join(gameId, _eric);
    _quizzy.Join(gameId, _martin);
    _quizzy.StartGame(gameId);
    

    var question = _quizzy.Question(gameId);
    _quizzy.Answer(gameId, _mathias, "answer1", 5);
    _quizzy.Answer(gameId, _eric, "answer1", 10);
    _quizzy.Answer(gameId, _martin, "answer1", 10);
    
    _quizzy.Answer(gameId, _martin, "answer2", 10);
    _quizzy.Answer(gameId, _mathias, "answer2", 10);
    _quizzy.Answer(gameId, _eric, "answer2", 5);
    
    test.equal("Eric", _quizzy.Winner(gameId));
    test.end();
})


tap.test("StartAQuiz_3PlayersJoin_EricAnswersAllCorrectlyAndFastest_EricIsWinner()", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    
    test.equal("Open", _quizzy.Status(gameId));
    test.equal([_existingQuiz].length, _quizzy.Games().length);
    test.equal([_existingQuiz][0], _quizzy.Games()[0]);

    _quizzy.Join(gameId, _mathias);
    _quizzy.Join(gameId, _eric);
    _quizzy.Join(gameId, _martin);
    _quizzy.StartGame(gameId);
    
    test.equal("Started", _quizzy.Status(gameId));

    var question = _quizzy.Question(gameId);
    _quizzy.Answer(gameId, _mathias, "answer1", 10);
    _quizzy.Answer(gameId, _eric, "answer1", 10);
    test.equal("question1", _quizzy.Question(gameId));
    _quizzy.Answer(gameId, _martin, "answer1", 10);
    test.equal("question2", _quizzy.Question(gameId));
    
    _quizzy.Answer(gameId, _martin, "answer2", 10);
    _quizzy.Answer(gameId, _mathias, "incorrect answer", 10);
    _quizzy.Answer(gameId, _eric, "answer2", 5);
    
    test.equal("Finished", _quizzy.Status(gameId));
    
    test.equal("Eric", _quizzy.Winner(gameId));
    test.end();
})

tap.test("Open_Existing_Quiz", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    test.equal("Open", _quizzy.Status(gameId));
    test.end();
})


tap.test("Open_NonExisting_Quiz()", (test) => {
    Setup();
    try {
        _quizzy.Open("non existing quiz");
    } catch (e) {
        test.equal("Quiz does not exist", e.Message);
        test.end();
    }
})


tap.test("StartingAnExistingGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _mathias);
    _quizzy.StartGame(gameId);
    test.equal("Started", _quizzy.Status(gameId));
    test.end();
})


tap.test("StartingANonExistingGame", (test) => {
    Setup();
    try {
        _quizzy.StartGame(uuid4());
    } catch (e) {
        test.equal("Game does not exist", e.Message);
        test.end();
    }
})


tap.test("ExistingPlayer_Joins_OpenGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _mathias);
    
    test.equal("Open", _quizzy.Status(gameId));
    test.end();
})


tap.test("NonExistingPlayer_Joins_AnExistingGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    try {
        _quizzy.Join(gameId, "Non existing player");
    } catch (exception) {
        test.equal("Player does not exist", exception.Message);
        test.end();
    }
})


tap.test("ExistingPlayer_Joins_NonExistingGame", (test) => {
    Setup();
    try {
        _quizzy.Join(uuid4(), _mathias);
    } catch (exception) {
        test.equal("Game does not exist", exception.Message);
        test.end();
    }
})


tap.test("ExistingPlayer_Joins_StartedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _eric);
    
    _quizzy.StartGame(gameId);
    try {
        _quizzy.Join(gameId, _mathias);
    } catch (exception) {
        test.equal("Game already started", exception.Message);
        test.end();
    }
})


tap.test("ExistingPlayer_Joins_ExistingGame_Twice()", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _mathias);

    try {
        _quizzy.Join(gameId, _mathias);
    } catch (exception) {
        test.equal("Player joined already", exception.Message);
        test.end();
    }
})


tap.test("ExistingPlayer_Joins_FinishedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.StartGame(gameId);

    try {
        _quizzy.Join(gameId, _mathias);
    } catch (exception) {
        test.equal("Game is finished", exception.Message);
        test.end();
    }
})


tap.test("ExistingGame_NoPlayerJoins_StartGame_StatusIs", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.StartGame(gameId);
    test.equal("Finished", _quizzy.Status(gameId));
    test.end();
})


tap.test("Question_On_NonExistingGame", (test) => {
    Setup();
    try {
        _quizzy.Question(uuid4());
    } catch (exception) {
        test.equal("Game does not exist", exception.Message);
        test.end();
    }
})


tap.test("Question_On_NotStartedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    try {
        _quizzy.Question(gameId);
    } catch (exception) {
        test.equal("Game is not started", exception.Message);
        test.end();
    }
})


tap.test("Question_On_FinishedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.StartGame(gameId);
    try {
        _quizzy.Question(gameId);
    } catch (exception) {
        test.equal("Game is finished", exception.Message);
        test.end();
    }
})


tap.test("Question_On_StartedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _eric);
    _quizzy.StartGame(gameId);
    
    test.equal("question1", _quizzy.Question(gameId));
    test.end();
})



tap.test("AnswerQuestion_On_NonExistingGame", (test) => {
    Setup();
    try {
        _quizzy.Answer(uuid4(), _martin, "", 0);
    } catch (exception) {
        test.equal("Game does not exist", exception.Message);
        test.end();
    }
})


tap.test("AnswerQuestion_On_NotStartedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    try {
        _quizzy.Answer(gameId, _martin, "", 0);
    } catch (exception) {
        test.equal("Game is not started", exception.Message);
        test.end();
    }
})


tap.test("AnswerQuestion_On_FinishedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.StartGame(gameId);
    try {
        _quizzy.Answer(gameId, _martin, "", 0);
    } catch (exception) {
        test.equal("Game is finished", exception.Message);
        test.end();
    }
})


tap.test("StartedGame_AnswerQuestionTwice", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _mathias);
    _quizzy.Join(gameId, _eric);
    
    _quizzy.StartGame(gameId);
    _quizzy.Answer(gameId, _mathias, "", 0);

    try {
        _quizzy.Answer(gameId, _mathias, "", 0);
    } catch (exception) {
        test.equal("Question already answered", exception.Message);
        test.end();
    }
})


tap.test("StartedGame_NonJoinedPlayer_AnswersQuestion", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _mathias);
    
    _quizzy.StartGame(gameId);

    try {
        _quizzy.Answer(gameId, _martin, "", 0);
    } catch (exception) {
        test.equal("Player has not joined", exception.Message);
        test.end();
    }
})


tap.test("StartedGame_AnswerAllQuestions", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _mathias);
    _quizzy.Join(gameId, _eric);
    
    _quizzy.StartGame(gameId);
    test.equal("question1", _quizzy.Question(gameId));
    _quizzy.Answer(gameId, _mathias, "", 0);
    _quizzy.Answer(gameId, _eric, "", 0);
    
    test.equal("question2", _quizzy.Question(gameId));
    _quizzy.Answer(gameId, _eric, "", 0);
    _quizzy.Answer(gameId, _mathias, "", 0);
    
    test.equal("Finished", _quizzy.Status(gameId));
    test.end();
})


tap.test("Status_On_NonExistingGame", (test) => {
    Setup();
    try {
        _quizzy.Status(uuid4());
    } catch (exception) {
        test.equal("Game does not exist", exception.Message);
        test.end();
    }
})


tap.test("Winner_On_NonExistingGame", (test) => {
    Setup();
    try {
        _quizzy.Winner(uuid4());
    } catch (exception) {
        test.equal("Game does not exist", exception.Message);
        test.end();
    }
})


tap.test("Winner_On_NotStartedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    try {
        _quizzy.Winner(gameId);
    } catch (exception) {
        test.equal("Game is not started", exception.Message);
        test.end();
    }
})


tap.test("Winner_On_StartedGame", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.Join(gameId, _eric);
    _quizzy.StartGame(gameId);
    try {
        _quizzy.Winner(gameId);
    } catch (exception) {
        test.equal("Game is started", exception.Message);
        test.end();
    }
})


tap.test("Winner_On_FinishedGame_WithoutPlayers", (test) => {
    Setup();
    var gameId = _quizzy.Open(_existingQuiz);
    _quizzy.StartGame(gameId);
    try {
        _quizzy.Winner(gameId);
    } catch (exception) {
        test.equal("Game is finished but no players", exception.Message);
        test.end();
    }
})