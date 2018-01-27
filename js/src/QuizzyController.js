const uuid4 = require("uuid/v4");

class Quiz
{
    constructor(name, questions) {
        this.name = name;
        this.questions = questions;
    }
}

class ArgumentException {
    constructor(message) {
        this.Message = message;
    }
}


class QuizzyController 
{
    constructor()
    {
        this.users = ["Mathias", "Eric", "Alberto", "Martin"];
        this._openQuizzes = {};
        var questions = [{"question1": "answer1"}, {"question2": "answer2"}];
        this._allQuizzes = {};
        this._allQuizzes["star wars"] = new Quiz("star wars", questions);
        this._startedQuizzes = {};
        this._joinedPlayers = {};
        this._finishedQuizzes = {};
        this._answers = {};
        this._currentQuestion = {};
    }

    Open(name)
    {
        if (this._allQuizzes[name])
        {
            var gameId = uuid4();
            this._openQuizzes[gameId] = name;
            this._joinedPlayers[gameId] = [];
            return gameId;
        }
        else
        {
            throw new ArgumentException("Quiz does not exist");
        }
    }

    Games()
    {
        return Object.values(this._openQuizzes);
    }

    StartGame(quizId)
    {
        if (!this._openQuizzes[quizId])
        {
            throw new ArgumentException("Game does not exist");
        }

        if (this._joinedPlayers[quizId].length === 0)
        {
            this._finishedQuizzes[quizId] = true;
        }
        else
        {
            this._startedQuizzes[quizId] = true;
            this._answers[quizId] = {};
            this._currentQuestion[quizId] = 0;
        }
    }

    Join(gameId, userName)
    {
        if (this.users.indexOf(userName) === -1)
        {
            throw new ArgumentException("Player does not exist");
        }

        if (!this._openQuizzes[gameId])
        {
            throw new ArgumentException("Game does not exist");
        }

        if (this._startedQuizzes[gameId])
        {
            throw new ArgumentException("Game already started");
        }

        if (this._joinedPlayers[gameId].indexOf(userName) !== -1)
        {
            throw new ArgumentException("Player joined already");
        }

        if (this._finishedQuizzes[gameId])
        {
            throw new ArgumentException("Game is finished");
        }


        this._joinedPlayers[gameId].push(userName);
    }

    Status(quizId) {
        if (this._finishedQuizzes[quizId])
        {
            return "Finished";
        }

        if (this._startedQuizzes[quizId])
        {
            return "Started";
        }

        if (this._openQuizzes[quizId])
        {
            return "Open";
        }

        throw new ArgumentException("Game does not exist");
    }

    Question(quizId)
    {
        if (!this._openQuizzes[quizId])
        {
            throw new ArgumentException("Game does not exist");
        }

        if (this._finishedQuizzes[quizId])
        {
            throw new ArgumentException("Game is finished");
        }

        if (!this._startedQuizzes[quizId])
        {
            throw new ArgumentException("Game is not started");
        }

        var questionId = this._currentQuestion[quizId];
        var quizName = this._openQuizzes[quizId];
        var quiz = this._allQuizzes[quizName];
        return Object.keys(quiz.questions[questionId])[0];
    }

    Answer(quizId, userName, answer, answerTime)
    {
        if (!this._openQuizzes[quizId])
        {
            throw new ArgumentException("Game does not exist");
        }

        if (this._finishedQuizzes[quizId])
        {
            throw new ArgumentException("Game is finished");
        }

        if (!this._startedQuizzes[quizId])
        {
            throw new ArgumentException("Game is not started");
        }

        if (!this._joinedPlayers[quizId].some(user => user === userName))
        {
            throw new ArgumentException("Player has not joined");
        }

        var answers = this._answers[quizId];
        var question = this._currentQuestion[quizId];
        if (answers[question])
        {
            var a = answers[question];

            if (a.some((tuple) => tuple[0] === userName))
            {
                throw new ArgumentException("Question already answered");
            }


            a.push([userName, [answer, answerTime]]);
        }
        else
        {
            answers[question] = [[userName, [answer, answerTime]]];
        }

        if (answers[question].length === this._joinedPlayers[quizId].length)
        {
            question = question + 1;
            this._currentQuestion[quizId] = question;
        }

        var quizName = this._openQuizzes[quizId];
        var quiz = this._allQuizzes[quizName];

        if (question >= quiz.questions.length)
        {
            this._finishedQuizzes[quizId] = true;
        }
    }

    Winner(gameName)
    {
        try
        {
            if (this.Status(gameName) === "Finished")
            {
                if (this._joinedPlayers[gameName].length === 0)
                    throw new ArgumentException("Game is finished but no players");

                var winner = "";
                var correctAnswerScore = 10;
                var scores = {};
                var quizName = this._openQuizzes[gameName];
                var quiz = this._allQuizzes[quizName];
                for (let i of Object.keys(this._answers[gameName]))
                {
                    var correct = Object.values(quiz.questions[i])[0];
                    var tuples = this._answers[gameName][i];

                    tuples.forEach(tuple =>
                    {
                        var user = tuple[0];
                        var answer = tuple[1][0];
                        var speed = tuple[1][1];

                        if (correct === answer)
                        {
                            let score = scores[user];
                            if (score)
                            {
                                if (speed === 0)
                                {
                                    scores[user] = score + (correctAnswerScore * (i +1));
                                }
                                else
                                {
                                    scores[user] = score + ((correctAnswerScore * (i+1))/ speed);
                                }
                            }
                            else
                            {
                                scores[user] = correctAnswerScore / speed;
                            }
                        }
                    });
                }

                var largest = 0;
                for (let user in scores)
                {
                    if (scores[user] > largest)
                    {
                        largest = scores[user];
                        winner = user
                    }
                }

                return winner;
            }

            if (this.Status(gameName) === "Open")
            {
                throw new ArgumentException("Game is not started");
            }

            if (this.Status(gameName) === "Started")
            {
                throw new ArgumentException("Game is started");
            }

            return "";
        } catch (e) {
            throw e;
        }
    }
}

module.exports.Quiz = Quiz;
module.exports.QuizzyController = QuizzyController;