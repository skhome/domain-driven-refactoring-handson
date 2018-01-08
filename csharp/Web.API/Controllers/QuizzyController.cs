using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;

namespace Web.API.Controllers
{
    public class Quiz {
        public string Name {get;}
        public IDictionary<string, string> Questions {get;}

        public Quiz(string name, IDictionary<string, string> questions) {
            Name = name;
            Questions = questions;
        }
    }

    [Route("api/[controller]")]
    public class QuizzyController : Controller
    {
        private IEnumerable<string> users;
        private readonly IDictionary<Guid, string> _openQuizzes;
        private readonly IDictionary<Guid, string> _startedQuizzes;
        private readonly IDictionary<string, Quiz> _allQuizzes;
        private readonly ISet<Guid> _finishedQuizzes;
        private readonly IDictionary<Guid, IList<string>> _joinedPlayers;
        private readonly IDictionary<Guid, IDictionary<int, List<Tuple<string, Tuple<string, int>>>>> _answers;
        private readonly IDictionary<Guid, int> _currentQuestion;

        public QuizzyController() {
            users = new List<string> { "Mathias", "Eric", "Alberto", "Martin"};
            _openQuizzes = new Dictionary<Guid, string>();
            var questions = new Dictionary<string, string>();
            questions.Add("question1", "answer1");
            questions.Add("question2", "answer2");
            _allQuizzes =
                new Dictionary<string, Quiz> {{"star wars", new Quiz("star wars", questions)}};
            _startedQuizzes = new Dictionary<Guid, string>();
            _joinedPlayers = new Dictionary<Guid, IList<string>>();
            _finishedQuizzes = new HashSet<Guid>();
            _answers = new Dictionary<Guid, IDictionary<int, List<Tuple<string, Tuple<string, int>>>>>();
            _currentQuestion = new Dictionary<Guid, int>();
        }

        public Guid Open(string name) {
            if (_allQuizzes.ContainsKey(name))
            {
                var gameId = Guid.NewGuid();
                _openQuizzes.Add(gameId, name);
                _joinedPlayers.Add(gameId, new List<string>());
                return gameId;
            }
            else
            {
                throw new ArgumentException("Quiz does not exist");
            }
        }

        public IEnumerable<string> Games()
        {
            return _openQuizzes.Values;
        }
        
        public void StartGame(Guid quizId)
        {
            if (!_openQuizzes.ContainsKey(quizId))
            {
                throw new ArgumentException("Game does not exist"); 
            }

            if (_joinedPlayers[quizId].Count == 0)
            {
                _finishedQuizzes.Add(quizId);
            }
            else
            {
                _startedQuizzes.Add(quizId, "");
                _answers.Add(quizId, new Dictionary<int, List<Tuple<string, Tuple<string, int>>>>());
                _currentQuestion.Add(quizId, 0);
            }
        }

        public void Join(Guid gameId, string userName) {
            if (!users.Contains(userName))
            {
                throw new ArgumentException("Player does not exist");
            }

            if (!_openQuizzes.ContainsKey(gameId))
            {
                throw new ArgumentException("Game does not exist");
            }

            if (_startedQuizzes.ContainsKey(gameId))
            {
                throw new ArgumentException("Game already started");
            }

            if (_joinedPlayers[gameId].Contains(userName))
            {
                throw new ArgumentException("Player joined already");
            }

            if (_finishedQuizzes.Contains(gameId))
            {
                throw new ArgumentException("Game is finished");
            }

            _joinedPlayers[gameId].Add(userName);
        }
        
        public string Status(Guid quizId)
        {
            if (_finishedQuizzes.Contains(quizId))
            {
                return "Finished";
            }

            if (_startedQuizzes.ContainsKey(quizId))
            {
                return "Started";
            }

            if (_openQuizzes.ContainsKey(quizId))
            {
                return "Open";
            }
            return "";
        }

        public string Question(Guid quizId)
        {
            if (!_openQuizzes.ContainsKey(quizId))
            {
                throw new ArgumentException("Game does not exist");
            }
            
            if (_finishedQuizzes.Contains(quizId))
            {
                throw new ArgumentException("Game is finished");
            }

            if (!_startedQuizzes.ContainsKey(quizId))
            {
                throw new ArgumentException("Game is not started");
            }

            var questionId = _currentQuestion[quizId];
            var quizName = _openQuizzes[quizId];
            var quiz = _allQuizzes[quizName];
            return quiz.Questions.ToArray()[questionId].Key;
        }

        public void Answer(Guid quizId, string userName, string answer, int answerTime)
        {
            if (!_openQuizzes.ContainsKey(quizId))
            {
                throw new ArgumentException("Game does not exist");
            }
            
            if (_finishedQuizzes.Contains(quizId))
            {
                throw new ArgumentException("Game is finished");
            }

            if (!_startedQuizzes.ContainsKey(quizId))
            {
                throw new ArgumentException("Game is not started");
            }

            if (!_joinedPlayers[quizId].Contains(userName))
            {
                throw new ArgumentException("Player has not joined"); 
            }

            var answers = _answers[quizId];
            var question = _currentQuestion[quizId];
            if (answers.ContainsKey(question))
            {
                var a = answers[question];
                
                if (a.Any((tuple) => tuple.Item1 == userName))
                {
                    throw new ArgumentException("Question already answered");
                }
                
                a.Add(new Tuple<string, Tuple<string, int>>(userName, new Tuple<string, int>(answer, answerTime)));
            }
            else
            {
                answers.Add(question, new List<Tuple<string, Tuple<string, int>>> { new Tuple<string, Tuple<string, int>>(userName, new Tuple<string, int>(answer, answerTime)) });
            }

            if (answers[question].Count == _joinedPlayers[quizId].Count)
            {
                question = question + 1;
                _currentQuestion[quizId] = question;
            }
            
            var quizName = _openQuizzes[quizId];
            var quiz = _allQuizzes[quizName];

            if (question >= quiz.Questions.Count)
            {
                _finishedQuizzes.Add(quizId);
            }
            
        }

        public string Winner(string gameName)
        {
            return "";
        }
    }
}
