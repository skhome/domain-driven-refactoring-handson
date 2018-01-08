using System;
using NUnit.Framework;
using Web.API.Controllers;
using System.Collections.Generic;

namespace Tests
{
    public class QuizzyControllerTest
    {
        private QuizzyController _quizzy;
        private string _existingQuiz;
        private string _mathias;
        private string _eric;
        private string _martin;

        [SetUp]
        public void Setup()
        {
            _quizzy = new QuizzyController();
            _existingQuiz = "star wars";

            _mathias = "Mathias";
            _eric = "Eric";
            _martin = "Martin";
        }
        
        #region scenario_tests

        [Test]
        public void EricAnswersMostCorrectly_EricIsWinner()
        {
            
        }

        [Test]
        public void StartAQuiz_3PlayersJoin_EricAnswersAllCorrectlyAndFastest_EricIsWinner() {
            var gameId = _quizzy.Open(_existingQuiz);
            
            Assert.AreEqual("Open", _quizzy.Status(gameId));
            Assert.AreEqual(new List<string> {_existingQuiz}, _quizzy.Games());

            _quizzy.Join(gameId, _mathias);
            _quizzy.Join(gameId, _eric);
            _quizzy.Join(gameId, _martin);
            _quizzy.StartGame(gameId);
            
            Assert.AreEqual("Started", _quizzy.Status(gameId));

            var question = _quizzy.Question(gameId);
            _quizzy.Answer(gameId, _mathias, "answer1", 10);
            _quizzy.Answer(gameId, _eric, "answer1", 10);
            Assert.AreEqual("question1", _quizzy.Question(gameId));
            _quizzy.Answer(gameId, _martin, "answer1", 10);
            Assert.AreEqual("question2", _quizzy.Question(gameId));
            
            _quizzy.Answer(gameId, _martin, "answer2", 10);
            _quizzy.Answer(gameId, _mathias, "incorrect answer", 10);
            _quizzy.Answer(gameId, _eric, "answer2", 5);
            
            Assert.AreEqual("Finished", _quizzy.Status(gameId));
            
            Assert.AreEqual("Eric", _quizzy.Winner(_existingQuiz));
        }
        #endregion
        
        #region unit tests

        [Test]
        public void Open_Existing_Quiz()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            Assert.AreEqual("Open", _quizzy.Status(gameId));
        }
        
        [Test]
        public void Open_NonExisting_Quiz() {
            Assert.Throws<ArgumentException>(() => _quizzy.Open("non existing quiz"));
        }

        [Test]
        public void StartingAnExistingGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _mathias);
            _quizzy.StartGame(gameId);
            Assert.AreEqual("Started", _quizzy.Status(gameId));
        }

        [Test]
        public void StartingANonExistingGame()
        {
            Assert.Throws<ArgumentException>(() => _quizzy.StartGame(Guid.Empty));
        }

        [Test]
        public void ExistingPlayer_Joins_OpenGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _mathias);
            
            Assert.AreEqual("Open", _quizzy.Status(gameId));
        }
        
        [Test]
        public void NonExistingPlayer_Joins_AnExistingGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Join(gameId, "Non existing player"));
            Assert.AreEqual("Player does not exist", exception.Message);
        }

        [Test]
        public void ExistingPlayer_Joins_NonExistingGame()
        {
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Join(Guid.Empty, _mathias));
            Assert.AreEqual("Game does not exist", exception.Message);
        }

        [Test]
        public void ExistingPlayer_Joins_StartedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _eric);
            
            _quizzy.StartGame(gameId);
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Join(gameId, _mathias));
            Assert.AreEqual("Game already started", exception.Message);
        }

        [Test]
        public void ExistingPlayer_Joins_ExistingGame_Twice() {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _mathias);

            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Join(gameId, _mathias));
            Assert.AreEqual("Player joined already", exception.Message);
        }

        [Test]
        public void ExistingPlayer_Joins_FinishedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.StartGame(gameId);

            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Join(gameId, _mathias));
            Assert.AreEqual("Game is finished", exception.Message);
        }

        [Test]
        public void ExistingGame_NoPlayerJoins_StartGame_StatusIs()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.StartGame(gameId);
            Assert.AreEqual("Finished", _quizzy.Status(gameId));
        }

        [Test]
        public void Question_On_NonExistingGame()
        {
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Question(Guid.Empty));
            Assert.AreEqual("Game does not exist", exception.Message);
        }

        [Test]
        public void Question_On_NotStartedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Question(gameId));
            Assert.AreEqual("Game is not started", exception.Message);
        }

        [Test]
        public void Question_On_FinishedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.StartGame(gameId);
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Question(gameId));
            Assert.AreEqual("Game is finished", exception.Message);
        }

        [Test]
        public void Question_On_StartedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _eric);
            _quizzy.StartGame(gameId);
            
            Assert.AreEqual("question1", _quizzy.Question(gameId));
        }
        

        [Test]
        public void AnswerQuestion_On_NonExistingGame()
        {
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Answer(Guid.Empty, _martin, "", 0));
            Assert.AreEqual("Game does not exist", exception.Message);
        }

        [Test]
        public void AnswerQuestion_On_NotStartedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Answer(gameId, _martin, "", 0));
            Assert.AreEqual("Game is not started", exception.Message);
        }

        [Test]
        public void AnswerQuestion_On_FinishedGame()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.StartGame(gameId);
            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Answer(gameId, _martin, "", 0));
            Assert.AreEqual("Game is finished", exception.Message);
        }

        [Test]
        public void StartedGame_AnswerQuestionTwice()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _mathias);
            _quizzy.Join(gameId, _eric);
            
            _quizzy.StartGame(gameId);
            _quizzy.Answer(gameId, _mathias, "", 0);

            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Answer(gameId, _mathias, "", 0));
            Assert.AreEqual("Question already answered", exception.Message);
        }

        [Test]
        public void StartedGame_NonJoinedPlayer_AnswersQuestion()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _mathias);
            
            _quizzy.StartGame(gameId);

            var exception = Assert.Throws<ArgumentException>(() => _quizzy.Answer(gameId, _martin, "", 0));
            Assert.AreEqual("Player has not joined", exception.Message);
        }

        [Test]
        public void StartedGame_AnswerAllQuestions()
        {
            var gameId = _quizzy.Open(_existingQuiz);
            _quizzy.Join(gameId, _mathias);
            _quizzy.Join(gameId, _eric);
            
            _quizzy.StartGame(gameId);
            Assert.AreEqual("question1", _quizzy.Question(gameId));
            _quizzy.Answer(gameId, _mathias, "", 0);
            _quizzy.Answer(gameId, _eric, "", 0);
            
            Assert.AreEqual("question2", _quizzy.Question(gameId));
            _quizzy.Answer(gameId, _eric, "", 0);
            _quizzy.Answer(gameId, _mathias, "", 0);
            
            Assert.AreEqual("Finished", _quizzy.Status(gameId));
        }
        #endregion
    }
}