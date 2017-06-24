package woogle.spi;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Locale;
import java.util.Stack;

import woogle.chart.CKYDecoder;
import woogle.chart.Chart;
import woogle.chart.ChartCell;
import woogle.chart.Path;
import woogle.cmd.Command;
import woogle.cmd.DigitLetterCommand;
import woogle.cmd.LowerLetterCommand;
import woogle.cmd.UpperLetterCommand;
import woogle.ds.PathNode;
import woogle.util.PinyinSyllable;
import woogle.util.Score;
import woogle.util.WoogleDatabase;

public class WooglePinyinHandler {

    public boolean isShiftDn;

    public boolean consumeKeyTypedAndPressed;

    public WoogleInputMethod woogleInputMethod;

    public Chart chart;

    public Score score;

    public PinyinSyllable pinyinSyllable;

    public WoogleState state;

    public CKYDecoder decoder;

    public Stack<Command> cmdManager;

    WooglePinyinHandler(WoogleInputMethod w) {
        this.woogleInputMethod = w;
        this.score = new Score(WoogleDatabase.nGramLanguageModel, WoogleDatabase.dependencyLanguageModel);
        this.pinyinSyllable = new PinyinSyllable();
        this.state = w.state;
        this.cmdManager = new Stack<Command>();
        this.isShiftDn = false;
        this.consumeKeyTypedAndPressed = false;
        this.chart = new Chart();
        this.decoder = new CKYDecoder(score);
    }


    public void keyPressed(char c) {
        Command cmd = new LowerLetterCommand(this, c);
        cmd.execute();
        cmdManager.push(cmd);
        this.consumeKeyTypedAndPressed = true;
    }

    public void downAction() {
        if (state.isSimplefiedChinese() && !state.isInputStringEmpty()) {
            if (!state.isLastPage())
                state.pageDown();
            this.consumeKeyTypedAndPressed = true;
        }
    }

    public void upAction() {
        if (state.isSimplefiedChinese() && !state.isInputStringEmpty()) {
            if (!state.isFirstPage())
                state.pageUp();
            this.consumeKeyTypedAndPressed = true;
        }
    }


    public void enterAction() {
        if (state.isSimplefiedChinese() && !state.isInputStringEmpty()) {
            WoogleLookupCandidate c = state.getCand(0);
            c.c.selectedIndex = c.pathIndex;

            if (state.isFirstPage())
                state.result.clearResult();

            state.result.pushChartCell(c.c);
            woogleInputMethod.sendText(state.result.toSendText());
            this.clear();
            this.consumeKeyTypedAndPressed = true;
        }
    }

    public void backspaceAction() {
        if (state.isSimplefiedChinese() && !state.isInputStringEmpty()) {
            Command cmd = cmdManager.pop();
            cmd.undo();
            this.consumeKeyTypedAndPressed = true;
        }
    }


    public void clear() {
        this.isShiftDn = false;
        this.consumeKeyTypedAndPressed = false;
        state.inputString = new StringBuilder();
        state.cands.clear();
        state.currentCandPage = 0;
        state.result.clearAll();
    }

    public void setCand() {
        state.cands.clear();
        state.currentCandPage = 0;
        HashSet<String> list = new HashSet<String>();
        ChartCell cell = decoder.getOneBestCell(chart);
        // System.out.println("--------------------");
        // System.out.println(cell);
        // System.out.println("--------------------");
        if (cell != null) {
            for (int i = 0; list.size() <= 3 && i < cell.sentences.size(); i++) {
                PathNode p = cell.getPath(i);
                String s = Path.getSentenceString(p);
                if (!list.contains(s)) {
                    state.cands.add(new WoogleLookupCandidate(i, cell));
                    list.add(s);
                }
            }
        }
        int row = state.result.selectedCell.size();
        for (int j = row; j < chart.num; j++) {
            cell = chart.getCell(row, j);
            if (cell == null)
                continue;
            for (int k = 0; k < cell.sentences.size(); k++) {
                PathNode p = cell.getPath(k);
                String s = Path.getSentenceString(p);
                if (s.length() == 1 && !list.contains(s)) {
                    state.cands.add(new WoogleLookupCandidate(k, cell));
                    list.add(s);
                }
            }
        }
    }


    public void sendText(String text) {
        woogleInputMethod.sendText(text);
    }

}