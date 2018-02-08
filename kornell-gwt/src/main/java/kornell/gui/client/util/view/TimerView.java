package kornell.gui.client.util.view;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class TimerView extends Composite {

    FlowPanel pnlWidget = new FlowPanel();
    Label lblTimer = new Label();

    public TimerView() {
        createComponents();
        // Positioning.betweenBars(pnlWidget);

        Timer countdown = new Timer() {
            int count = 0;

            @Override
            public void run() {
                lblTimer.setText(++count + "...");
                schedule(1000);
            }
        };
        countdown.schedule(1000);

        initWidget(pnlWidget);
    }

    private void createComponents() {
        pnlWidget.getElement().setAttribute("style", "background-color: red; margin-top: 45px; height: 100px;");
        lblTimer.setText("Time!");
        lblTimer.getElement().setAttribute("style", "font-size: 10em; color: white;");
        pnlWidget.add(lblTimer);
    }

}
