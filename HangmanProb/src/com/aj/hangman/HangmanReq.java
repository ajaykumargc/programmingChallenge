/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.aj.hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @version $Id$
 */
public class HangmanReq
{
    public static String TOKEN = "";

    public static String STATUS = "";

    public static String STATE = "";

    public static Long REM, PREM;

    public static String guess;

    public static void main(String[] args)
    {
        HangmanDict dictionary = new HangmanDict();

        try
        {
            BufferedReader input =
                new BufferedReader(new InputStreamReader(
                    new URL("http://gallows.hulu.com/play?code=gudehosa@usc.edu").openStream()));
            String info = input.readLine();
            JSONParser parser = new JSONParser();

            Object obj;
            try
            {
                obj = parser.parse(info);
                JSONObject retJson = (JSONObject) obj;

                TOKEN = (String) retJson.get("token");
                STATUS = (String) retJson.get("status");
                STATE = (String) retJson.get("state");
                REM = (Long) retJson.get("remaining_guesses");
                PREM = REM;
                System.out.println("State:: " + STATE);
            }
            catch (ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while ("ALIVE".equalsIgnoreCase(STATUS))
            {
                // call make guess function, returns character
                guess = dictionary.makeGuess(STATE);
                System.out.println("Guessed:: " + guess);
                // call the url to update
                BufferedReader reInput =
                    new BufferedReader(new InputStreamReader(new URL(
                        "http://gallows.hulu.com/play?code=gudehosa@usc.edu"
                            + String.format("&token=%s&guess=%s", TOKEN, guess)).openStream()));

                // parse the url to get the updated value
                String reInfo = reInput.readLine();
                JSONParser reParser = new JSONParser();

                Object retObj = reParser.parse(reInfo);
                JSONObject retJson = (JSONObject) retObj;

                STATUS = (String) retJson.get("status");
                STATE = (String) retJson.get("state");
                REM = (Long) retJson.get("remaining_guesses");
                System.out.println("State:: " + STATE);
            }

            if ("DEAD".equalsIgnoreCase(STATUS))
            {
                // print lost
                System.out.println("You LOOSE: DEAD");
            }
            else if ("FREE".equalsIgnoreCase(STATUS))
            {
                // print free
                System.out.println("You WIN: FREE");
            }

        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
