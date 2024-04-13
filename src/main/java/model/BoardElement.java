package model;

import lombok.Getter;

@Getter
public enum BoardElement {
    CROSS(1),
    CIRCLE(-1),
    NULL(0);

    private final int value;

    BoardElement(int value) {
        this.value = value;
    }

    public static BoardElement getBoardElement(int value) {
        return switch (value) {
            case -1 -> CIRCLE;
            case 1 -> CROSS;
            default -> NULL;
        };
    }

    public BoardElement getOpposite() {
        return switch (this) {
            case CROSS -> CIRCLE;
            case CIRCLE -> CROSS;
            default -> NULL;
        };
    }
}

