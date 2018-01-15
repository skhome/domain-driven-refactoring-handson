# Domain-Driven-Refactoring Hands-on

This repository contains the code and instructions for the Domain-Driven-Refactoring hands-on at [DDD Europe](https://dddeurope.com/2018/speakers/thomas-coopman/#handson)


# The hands-on

If you join this workshop, please follow this step by step instruction. You can start by reading this introduction and then go on to the first step.

The format of the hands-on is that some things can be done at your own pace (the pace of the pair) and some things will be done in smaller groups. The part to do in small groups will be announced, so don't worry about that.

## 1. Create a pair

Pair up with someone you want. It might be nice to pair with someone you don't know, or someone who doesn't know the language you picked, or...

*As soon as you have formed a pair you can go on to step 2.*

## 2. Pick a language

Please pick clone or fork the project (I'd love pull requests to correct mistakes, add languages, improve things), pick a language and make sure you can run the tests. All the test should be green.

*As soon as you're done with this go to step 2.*

### Languages available

* C# (.net core)
* Java 8 (with Gradle)

## 3. Explore the code

The background of the domain and the code. For those of you who did the *playing with projections* hands-on you will recognize the domain. A small story with the domain

    Last year the quizzy startup created a online quiz platform that was entirerly event based. It was too bad that it wasn't a real working system. So this year instead of a simulation, a real working platform has been implemented. Unfortunatly this experiment seems to have failed as well, because the code is a real mess. This is what you get from doing a bad form of TTD without any refactoring at all!

    So this time the quizzy startup wants your help to improve the code. Luckely they at least good view on the domain.

    Some terms that are important in the domain (the glossary)

    * Quiz
    * Game (an instance of a quiz)
    * Open a game
    * Start a game
    * Finish a game
    * Winner
    * Player
    * Answer a question
    * Subscriptions

    The interaction of the quiz/game can be viewed in the domain of last year:
    https://github.com/michelgrootjans/playing_with_projections/wiki

With this in mind, take a short time to check the code (5')

*The next step will be announced.*

## 4. Wall of smells

This is a group assignment. Please form groups of about 5-6 people. Pick a flipchart, post-its and markers. The assignment will be announced in group.

Some resources that can be useful:
* The code
* https://sourcemaking.com/refactoring/smells

*The next step will be announced.*

## 5. 15 minutes of refactoring

This is the first time you can start refactoring the code. Think about the smells you've identified in the previous step.

Things to think about:

* make sure the tests stay green
* commit often
* think about the smells

*The next step will be announced.*

## 6. Smells, refactoring and DDD

This is a group assignment. Please form groups of about 5-6 people. Pick a flipchart, post-its and markers. The assignment will be announced in group.

Some resources that can be useful:
* https://sourcemaking.com/refactoring

*The next step will be announced.*

## 7. The story

You've been given this story, please read it:

    Quizzy wants to introduce a playing subscription model for players. They would like to provide 2 types of subscriptions:

        1. Buy a number of games: you can play as many games as you bought up front. As soon as you join a game and that game is started, you subtract a credit.
        2. A monthly subscription: you can play as many games as you want in the month.
        3. There should also be a trial period, a player can subscribe for a trial period of one month and can play a maximum of 10 games in that month.

    Depending on these subscriptions, the winner can be different. In case of a tie the winner gets picked like this:
        *. Monthly subscription wins from credits wins from trial.
        *. In case of 2 monthly subscriptions tieing for the first place, the player who has the subscription for the longest period wins
        *. In case of 2 credits winning, the person who bought the highest amount of credits wins.
        *. In case of 2 trial players winning, the person whose trial ends first wins.

    Business has been told that the code is a real mess and so the team could negotiate that you can first refactor some parts, to better be able to implement the new requirements. As soon as the first refactorings are done, a first story should be implemented though. The team can decide the first story together with the product owner.
    Keep in mind that business would like the subscription model to be handed over to a different team once the basics have been implemented. The goal is that this team will add more subscriptions in the future.

    Another part is that we want to provide the people with more power over how they manage their quiz, one thing that gets asked a lot is that some questions should score you more points than other questions, so the quizmaster should be able to set how many points you get for a question. It’s not decided if this should be something on a quiz or game basis.

    There have been some ideas on future features but nothing decisive yet, for example creating a quiz should probably also be something that cost you some credits, but on the other hand, creating a popular quiz should gain you some credits.
    Quizzy wants to be able to adopt quickly to new trends in the quizzing scene.

After you've read the story, please discus what you think would be valuable to refactor, and what wouldn't be.

*The next step will be announced.*

## 8. Refactor

Think about the story, about all the steps leading up to this and start the refactoring, with this important rule:

### Rule: 

1. Do 1 refactoring (for example rename method)
2. Run the tests, all should be green (if not revert: `git reset --hard`)
3. If the tests are green, commit. With this message: **DDD Smell + refactoring name + optional extra info**

Please follow it at all times.
At any moment running: `git log --oneline` should give you a very clear history of what you have been doing.
It should look like:
```
...
23af32a No use of Ubiquitous Language - rename method - an instance of a quiz is a game
71834b0 No use of Ubiquitous Language - rename variable - user to player
...
```

*The next step will be announced.*

## 9. Conclusion

Conclusion and feedback

# Forking/Adding a language

The requirements for adding a language are:

* the code should match the java/c# code as close as possible
* make sure there is one command to run all the tests (with the same tests)
* little to no dependencies would be great!