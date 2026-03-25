package tests;

import utils.UnionFind;

public class TestUF {
    public void runAllTests() {
        testInitialState();
        testUnionAndFind();
        testTransitivity();
        testCopy();
    }

    private void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Error " + message);
        }
    }

    public void testInitialState() {
        UnionFind uf = new UnionFind(3);

        check(uf.find(0) == 0, "Initial: chaque élément doit être sa racine");

        System.out.println("testInitialState réussi");
    }

    public void testUnionAndFind() {
        UnionFind uf = new UnionFind(3);

        uf.union(0, 1);

        check(uf.isConnected(0, 1), "Union: 0 et 1 doivent être connectés");

        System.out.println("testUnionAndFind réussi");
    }

    public void testTransitivity() {
        UnionFind uf = new UnionFind(3);

        uf.union(0, 1);
        uf.union(1, 2);

        check(uf.isConnected(0, 2), "Transitivité: 0 et 2 doivent être connectés");

        System.out.println("testTransitivity réussi");
    }

    public void testCopy() {
        UnionFind uf1 = new UnionFind(3);

        uf1.union(0, 1);

        UnionFind uf2 = uf1.copy();

        uf1.union(1, 2);

        check(!uf2.isConnected(0, 2), "Copy: la copie ne doit pas changer");

        System.out.println("testCopy réussi");
    }
}
