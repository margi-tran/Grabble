package margi_tran.grabble;

/**
 *
    LetterPoints.java
 *
 *  This class represents letters with their points through an Enum.
 *
 *  @author Margi Tran */

public enum LetterPoints {

    A(3),
    B(20),
    C(13),
    D(10),
    E(1),
    F(15),
    G(18),
    H(9),
    I(5),
    J(25),
    K(22),
    L(11),
    M(14),
    N(6),
    O(4),
    P(19),
    Q(24),
    R(8),
    S(7),
    T(2),
    U(12),
    V(21),
    W(17),
    X(23),
    Y(16),
    Z(26);

    private final int value;

    LetterPoints(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
