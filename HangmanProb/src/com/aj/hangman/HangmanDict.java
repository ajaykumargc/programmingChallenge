package com.aj.hangman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version $Id$
 */
public class HangmanDict
{
    private Map<Integer, List<String>> wordCollection = new HashMap<Integer, List<String>>();

    private Map<String, Long> feasibleMap = new HashMap<String, Long>();

    private Set<Character> guesses = new HashSet<Character>();

    public HangmanDict()
    {
        initDict();
    }

    // parsing the downloaded files from net as reference dictionary

    void initDict()
    {
        try
        {
            File[] files = new File("scowlist/").listFiles();
            for (File eachFile : files)
            {
                BufferedReader reader = new BufferedReader(new FileReader(eachFile));
                for (String str; (str = reader.readLine()) != null;)
                {
                    str = str.trim();
                    if (!wordCollection.containsKey(str.length()))
                    {
                        wordCollection.put(str.length(), new ArrayList<String>());
                    }
                    wordCollection.get(str.length()).add(str.toLowerCase());
                }
            }
        }
        catch (IOException e)
        {
            System.err.println(e);
        }

        wordCollection.size();
    }

    public String makeGuess(String state)
    {
        String words[] = state.split(" ");
        int i = 0;

        while (i < words.length)
        {

            String word = words[i];
            String temp = word;
            i++;
            if (word.contains("_"))
            {
                int len = word.length();
                word = word.replace("_", "[a-z]");
                word += "$";
                word = "^" + word;
                Pattern regex = Pattern.compile(word);
                for (String guess : wordCollection.get(len))
                {
                    Matcher match = regex.matcher(guess);
                    if (match.find())
                    {
                        // add to feasible list
                        // feasibleWords.add(guess);
                        CharacterIterator it = new StringCharacterIterator(guess);
                        int k = 0;
                        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next())
                        {

                            if ("_".equalsIgnoreCase(Character.toString(temp.charAt(k))))
                            {
                                if (feasibleMap.containsKey(Character.toString(ch)))
                                {
                                    long val = feasibleMap.get(Character.toString(ch));
                                    ++val;
                                    feasibleMap.put(Character.toString(ch), val);
                                }
                                else
                                {
                                    long val = 1;
                                    feasibleMap.put(Character.toString(ch), val);
                                }
                            }
                            k++;
                        }
                    }
                }
            }

        }

        /*
         * // loop through the map and remove already guessed values for (Entry<String, Long> entry :
         * feasibleMap.entrySet()) { if (guesses.contains(entry.getKey())) { Long x = (long) 0;
         * feasibleMap.put(entry.getKey(), x); } }
         */
        // lop through to remove alredy guessed ones
        for (char help : guesses)
        {
            feasibleMap.remove(Character.toString(help));
        }

        // now check for the max frequent character
        Entry<String, Long> maxEntry = null;

        for (Entry<String, Long> entry : feasibleMap.entrySet())
        {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue())
            {
                maxEntry = entry;
            }
        }
        guesses.add(maxEntry.getKey().charAt(0));
        return maxEntry.getKey();
    }
    /*
     * public static void main(String[] args) { HangmanDict test = new HangmanDict(); }
     */
}
