const tap = require('tap');
const Quiz = require('../src/QuizzyController').Quiz;

tap.test('new quiz', (test) => {
    const quiz = new Quiz("Name");
    test.equal("Name", quiz.name);
    test.end();
})