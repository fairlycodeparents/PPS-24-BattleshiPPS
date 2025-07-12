package it.unibo.shipps.model;

public class PlayerBoardTest {

    // This class is a placeholder for the PlayerBoard test cases.
    // You can implement your test cases here using your preferred testing framework.
    // For example, you can use JUnit or ScalaTest to write tests for the PlayerBoard class.

    // Example of a test case (using JUnit):

    @Test
    public void testPlayerBoardInitialization() {
        PlayerBoard board = new PlayerBoard();
        assertNotNull(board);
        assertEquals(0, board.getScore());
    }
}

