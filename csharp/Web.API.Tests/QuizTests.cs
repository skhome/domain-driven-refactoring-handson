using NUnit.Framework;
using Web.API.Controllers;
using System.Collections.Generic;

namespace Tests
{
    public class QuizTests
    {
        [SetUp]
        public void Setup()
        {
        }

        [Test]
        public void NewQuiz()
        {
            var quiz = new Quiz("Name", new Dictionary<string, string>());

            Assert.AreEqual("Name", quiz.Name);
        }
    }
}