package com.example.namh;

import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;

import java.util.List;

public class AmharicIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private LinearLayout suggestionContainer;

    @Override
    public View onCreateInputView() {
        // Inflate the container layout
        View layout = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        kv = (KeyboardView) layout.findViewById(R.id.keyboard);
        suggestionContainer = (LinearLayout) layout.findViewById(R.id.suggestion_container);

        // Load your staggered Amharic XML
        keyboard = new Keyboard(this, R.xml.keyboard_amharic_full);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);

        // Disable the old floating preview for a cleaner look
        kv.setPreviewEnabled(false);

        return layout;
    }

    @Override
    public void onPress(int primaryCode) {
        // As soon as a key is touched, show the sub-letters at the top
        updateSuggestionBar(primaryCode);
    }

    private void updateSuggestionBar(int primaryCode) {
        if (suggestionContainer == null) return;

        suggestionContainer.removeAllViews();

        List<Keyboard.Key> keys = keyboard.getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == primaryCode && key.popupCharacters != null) {
                String subLetters = key.popupCharacters.toString();

                for (int i = 0; i < subLetters.length(); i++) {
                    final String letter = String.valueOf(subLetters.charAt(i));

                    TextView tv = new TextView(this);
                    tv.setText(letter);
                    tv.setTextSize(26);
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setPadding(40, 10, 40, 10);
                    tv.setGravity(Gravity.CENTER);

                    // Load your custom Ethiopic font
                    try {
                        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NotoSansEthiopic_Condensed-Regular.ttf");
                        tv.setTypeface(tf);
                    } catch (Exception e) { e.printStackTrace(); }

                    // THE LOGIC: When a sub-letter is tapped, it REPLACES the base letter
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputConnection ic = getCurrentInputConnection();
                            if (ic != null) {
                                // Overwrite the underlined "composing" text
                                ic.setComposingText(letter, 1);
                                // Finalize the choice
                                ic.finishComposingText();
                                suggestionContainer.removeAllViews();
                            }
                        }
                    });

                    suggestionContainer.addView(tv);
                }
                break;
            }
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case -5: // Delete
                ic.deleteSurroundingText(1, 0);
                suggestionContainer.removeAllViews();
                break;
            case 32: // Space
                ic.finishComposingText();
                ic.commitText(" ", 1);
                suggestionContainer.removeAllViews();
                break;
            case 10: // Enter
                ic.finishComposingText();
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case 0: // Spacer keys
                break;
            default:
                char code = (char) primaryCode;
                // Start a "Composing" state (underlined)
                // This allows the top bar to replace this letter if clicked
                ic.setComposingText(String.valueOf(code), 1);
        }
    }

    // Boilerplate requirements
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.finishComposingText();
            ic.commitText(text, 1);
        }
    }
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}