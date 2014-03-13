/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package open.dolphin.util;

import static java.lang.Thread.sleep;
import javafx.concurrent.Task;
import javafx.scene.text.Text;

/**
 * BlinkText Class
 *
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class BlinkText {

    Text text;
    String bkSt;

    public BlinkText(Text text) {
        this.text = text;
        this.bkSt = text.getText();
    }

    public void run() {

        Task<Integer> task = new Task<Integer>() {

            @Override
            protected Integer call() throws Exception {

                for (int i = 0; i <= 2; i++) {
                    blink();
                    // System.out.println("thread text.");
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    public void blink() throws InterruptedException {
        text.setText(bkSt);
        sleep(600);
        text.setText("");
        sleep(300);
    }
}
