import java.util.*;

public class QuizzyController {
    private Collection<String> users;
    private HashMap<UUID, String> openQuizzes;
    private HashMap<UUID, String> startedQuizzes;
    private HashMap<String, Quiz> allQuizzes;
    private Set<UUID> finishedQuizzes;
    private HashMap<UUID, Collection<String>> joinedPlayers;
    private HashMap<UUID, HashMap<Integer, Collection<Tuple<String, Tuple<String, Integer>>>>> answers;
    private HashMap<UUID, Integer> currentQuestion;

    public QuizzyController() {
        users = new ArrayList<>();
        users.add("Mathias");
        users.add("Eric");
        users.add("Alberto");
        users.add("Martin");

        openQuizzes = new HashMap<>();

        SortedMap<String, String> questions = new TreeMap<>();
        questions.put("question1", "answer1");
        questions.put("question2", "answer2");

        allQuizzes = new HashMap<>();
        allQuizzes.put("star wars", new Quiz("star wars", questions));
        startedQuizzes = new HashMap<>();
        joinedPlayers = new HashMap<>();
        finishedQuizzes = new HashSet<UUID>();
        answers = new HashMap<>();
        currentQuestion = new HashMap<>();
    }

    public UUID open(String name)
    {
        if (allQuizzes.containsKey(name))
        {
            UUID gameId = UUID.randomUUID();
            openQuizzes.put(gameId, name);
            joinedPlayers.put(gameId, new ArrayList<>());
            return gameId;
        }
        else
        {
            throw new IllegalArgumentException("Quiz does not exist");
        }
    }

    public Collection<String> games()
    {
        return openQuizzes.values();
    }

    public void startGame(UUID quizId)
    {
        if (!openQuizzes.containsKey(quizId))
        {
            throw new IllegalArgumentException("Game does not exist");
        }

        if (joinedPlayers.get(quizId).size() == 0)
        {
            finishedQuizzes.add(quizId);
        }
        else
        {
            startedQuizzes.put(quizId, "");
            answers.put(quizId, new HashMap<Integer, Collection<Tuple<String,Tuple<String,Integer>>>>());
            currentQuestion.put(quizId, 0);
        }
    }

    public void join(UUID gameId, String userName)
    {
        if (!users.contains(userName))
        {
            throw new IllegalArgumentException("Player does not exist");
        }

        if (!openQuizzes.containsKey(gameId))
        {
            throw new IllegalArgumentException("Game does not exist");
        }

        if (startedQuizzes.containsKey(gameId))
        {
            throw new IllegalArgumentException("Game already started");
        }

        if (joinedPlayers.get(gameId).contains(userName))
        {
            throw new IllegalArgumentException("Player joined already");
        }

        if (finishedQuizzes.contains(gameId))
        {
            throw new IllegalArgumentException("Game is finished");
        }

        joinedPlayers.get(gameId).add(userName);
    }

    public String status(UUID quizId)
    {
        if (finishedQuizzes.contains(quizId))
        {
            return "Finished";
        }

        if (startedQuizzes.containsKey(quizId))
        {
            return "Started";
        }

        if (openQuizzes.containsKey(quizId))
        {
            return "Open";
        }

        throw new IllegalArgumentException("Game does not exist");
    }

    public String question(UUID quizId)
    {
        if (!openQuizzes.containsKey(quizId))
        {
            throw new IllegalArgumentException("Game does not exist");
        }

        if (finishedQuizzes.contains(quizId))
        {
            throw new IllegalArgumentException("Game is finished");
        }

        if (!startedQuizzes.containsKey(quizId))
        {
            throw new IllegalArgumentException("Game is not started");
        }

        Integer questionId = currentQuestion.get(quizId);
        String quizName = openQuizzes.get(quizId);
        Quiz quiz = allQuizzes.get(quizName);
        String question = (String)quiz.questions.keySet().toArray()[questionId];
        return question;
    }

    public void answer(UUID quizId, String userName, String answer, Integer answerTime)
    {
        if (!openQuizzes.containsKey(quizId))
        {
            throw new IllegalArgumentException("Game does not exist");
        }

        if (finishedQuizzes.contains(quizId))
        {
            throw new IllegalArgumentException("Game is finished");
        }

        if (!startedQuizzes.containsKey(quizId))
        {
            throw new IllegalArgumentException("Game is not started");
        }

        if (!joinedPlayers.get(quizId).contains(userName))
        {
            throw new IllegalArgumentException("Player has not joined");
        }

        HashMap<Integer, Collection<Tuple<String, Tuple<String, Integer>>>> answers = this.answers.get(quizId);
        Integer question = currentQuestion.get(quizId);
        if (answers.containsKey(question))
        {
            Collection<Tuple<String, Tuple<String, Integer>>> a = answers.get(question);

            if (a.stream().anyMatch((tuple) -> tuple.item1 == userName))
            {
                throw new IllegalArgumentException("question already answered");
            }

            a.add(new Tuple<>(userName, new Tuple<>(answer, answerTime)));
        }
        else
        {
            List<Tuple<String, Tuple<String, Integer>>> newAnswer = new ArrayList<>();
            newAnswer.add(new Tuple<>(userName, new Tuple<>(answer, answerTime)));
            answers.put(question, newAnswer);
        }

        if (answers.get(question).size() == joinedPlayers.get(quizId).size())
        {
            question = question + 1;
            currentQuestion.put(quizId, question);
        }

        String quizName = openQuizzes.get(quizId);
        Quiz quiz = allQuizzes.get(quizName);

        if (question >= quiz.questions.size())
        {
            finishedQuizzes.add(quizId);
        }
    }

    public String winner(UUID gameName)
    {
        try
        {
            if (status(gameName) == "Finished")
            {
                if (joinedPlayers.get(gameName).size() == 0)
                    throw new IllegalArgumentException("Game is finished but no players");

                String winner = "";
                Integer correctAnswerScore = 10;
                HashMap<String, Integer> scores = new HashMap<>();
                String quizName = openQuizzes.get(gameName);
                Quiz quiz = allQuizzes.get(quizName);

                for (int i = 0; i < answers.get(gameName).size(); i++) {
                    String correct = (String)quiz.questions.values().toArray()[i];
                    Collection<Tuple<String, Tuple<String, Integer>>> tuples = answers.get(gameName).get(i);

                    int finalI = i;
                    tuples.stream().forEach(tuple ->
                            {
                                String user = tuple.item1;
                                String answer = tuple.item2.item1;
                                Integer speed = tuple.item2.item2;

                    if (correct == answer)
                    {
                        Integer score = scores.getOrDefault(user, -1);
                        if (score > 0) {
                            if (speed == 0)
                            {
                                scores.put(user, score + (correctAnswerScore * (finalI +1)));
                            }
                            else
                            {
                                scores.put(user, score + ((correctAnswerScore * (finalI +1))/ speed));
                            }
                        }
                        else
                        {
                            scores.put(user, correctAnswerScore / speed);
                        }
                    }
                        });
                }

                Integer largest = 0;
                for (int i = 0; i < scores.values().size(); i++) {
                    Object user = (scores.keySet().toArray()[i]);
                    Integer score = (Integer)scores.values().toArray()[i];
                    if (score > largest)
                    {
                        largest = score;
                        winner = (String)user;
                    }
                }

                return winner;
            }

            if (status(gameName) == "Open")
            {
                throw new IllegalArgumentException("Game is not started");
            }

            if (status(gameName) == "Started")
            {
                throw new IllegalArgumentException("Game is started");
            }


            return "";
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
    }

}
