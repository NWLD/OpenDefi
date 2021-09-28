package com.nwld.defi.tools.widget;

import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.util.StringUtil;

public class TextChangedAction {
    public interface Action {
        void action();

        String getText();
    }

    private String currentText;

    public void textChangedAction(String text, Action action) {
        this.currentText = text;
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.ignoreE(currentText, action.getText())) {
                    action.action();
                }
            }
        }, 200);
    }
}

