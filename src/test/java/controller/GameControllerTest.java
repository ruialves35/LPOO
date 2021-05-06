package controller;

import gui.GUI;
import gui.MouseListener;
import model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import viewer.GameViewer;

import java.io.IOException;

public class GameControllerTest {
    private GUI gui;
    private GameControllerState state;
    private GameViewer viewer;
    private GameController controller;
    private MouseListener mouseListener;

    @BeforeEach
    public void setUp() throws IOException {
        gui = Mockito.mock(GUI.class);
        state = Mockito.mock(GameControllerState.class);
        viewer = Mockito.mock(GameViewer.class);

        Mockito.doAnswer(invocation -> {
            this.mouseListener = invocation.getArgument(0);
            return null;
        }).when(gui).setMouseListener(Mockito.any());
        Mockito.when(gui.getNextAction()).thenReturn(GUI.ACTION.NONE);

        Mockito.when(state.getViewer()).thenReturn(viewer);

        controller = new GameController(gui, state);
    }

    @Test
    public void runFrame() throws IOException {
        controller.runFrame(50);

        Mockito.verify(viewer, Mockito.times(1)).drawScreen(gui);
        Mockito.verify(state, Mockito.times(1)).reactKeyboard(GUI.ACTION.NONE);
        Mockito.verify(state, Mockito.times(1)).reactTimePassed(50);

        Mockito.when(gui.getNextAction()).thenReturn(GUI.ACTION.INTERACT);
        controller.runFrame(250);

        Mockito.verify(viewer, Mockito.times(2)).drawScreen(gui);
        Mockito.verify(state, Mockito.times(1)).reactKeyboard(GUI.ACTION.INTERACT);
        Mockito.verify(state, Mockito.times(2)).reactTimePassed(250);
    }

    @Test
    public void run() throws IOException {
        Mockito.when(gui.getNextAction()).thenReturn(
                GUI.ACTION.NONE, GUI.ACTION.NONE, GUI.ACTION.INTERACT, GUI.ACTION.MOVE_LEFT, GUI.ACTION.QUIT);

        controller.run();

        Mockito.verify(viewer, Mockito.times(5)).drawScreen(gui);
        Mockito.verify(state, Mockito.times(2)).reactKeyboard(GUI.ACTION.NONE);
        Mockito.verify(state, Mockito.times(1)).reactKeyboard(GUI.ACTION.MOVE_LEFT);
        Mockito.verify(state, Mockito.never()).reactKeyboard(GUI.ACTION.QUIT);
    }

    @Test
    public void onMouseMovement() {
        mouseListener.onMouseMovement(4, 9);
        Mockito.verify(state, Mockito.times(1)).reactMouseMovement(new Position(4, 9));
    }

    @Test
    public void onMouseClick() {
        mouseListener.onMouseClick(5, 7);
        Mockito.verify(state, Mockito.times(1)).reactMouseClick(new Position(5, 7));
    }
}