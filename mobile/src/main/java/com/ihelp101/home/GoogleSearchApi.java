package com.ihelp101.home;

import android.content.Context;
import android.content.Intent;

public class GoogleSearchApi
{

    public static final String INTENT_NEW_SEARCH = "com.mohammadag.googlesearchapi.NEW_SEARCH";
    private static final String INTENT_REQUEST_SPEAK = "com.mohammadag.googlesearchapi.SPEAK";
    public static final String KEY_QUERY_TEXT = "query_text";
    public static final String KEY_TEXT_TO_SPEAK = "text_to_speak";
    public static final String KEY_VOICE_TYPE = "is_voice_search";

    public GoogleSearchApi()
    {
    }

    public static void speak(Context context, String s)
    {
        Intent intent = new Intent("com.mohammadag.googlesearchapi.SPEAK");
        intent.putExtra("text_to_speak", s);
        context.sendBroadcast(intent);
    }
}