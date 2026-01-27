package model;

public class UnionFind {
    private int[] parent;
    private int[] size;
    private final int N; // Nombre de cases du plateau== 196 pour 14*14)
    
    // noeuds virtuels
    private final int VIRTUAL_TOP;
    private final int VIRTUAL_BOTTOM;
    private final int VIRTUAL_LEFT;
    private final int VIRTUAL_RIGHT;

    public UnionFind(int boardSize) {
        this.N = boardSize * boardSize;
        
        // +4 pour avoir les bords virtuels
        this.parent = new int[N + 4];
        this.size = new int[N + 4];
        
        //bornes
        this.VIRTUAL_TOP    = N;
        this.VIRTUAL_BOTTOM = N + 1;
        this.VIRTUAL_LEFT   = N + 2;
        this.VIRTUAL_RIGHT  = N + 3;

        // Initialisation des parents : tout le monde est sa propre racine au début
        // On va jusqu'à n + 4 pour initialiser aussi les bords virtuels
        for (int i = 0; i < N + 4; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    // constructeur privé -> copie mcts => ne pas boucler en plus 
    private UnionFind(int[] parent, int[] size, int n) {
        this.parent = parent;
        this.size = size;
        this.N = n;
        this.VIRTUAL_TOP    = N;
        this.VIRTUAL_BOTTOM = N + 1;
        this.VIRTUAL_LEFT   = N + 2;
        this.VIRTUAL_RIGHT  = N + 3;
    }

    public int find(int p) {
        int root = p;
        //trouve la racine
        while (root != parent[root]) {
            root = parent[root];
        }
        // Compression de chemin
        while (p != root) {
            int newp = parent[p];
            parent[p] = root;
            p = newp;
        }
        return root;
    }

    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        
        if (rootP == rootQ) return;

        // attache les petits arbres aux gros
        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
    }

    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }

    // copie pour mcts
    public UnionFind copy() {
        int totalSize = parent.length; 
        int[] newParent = new int[totalSize];
        int[] newSize = new int[totalSize];
        
        System.arraycopy(this.parent, 0, newParent, 0, totalSize);
        System.arraycopy(this.size, 0, newSize, 0, totalSize);
        
        return new UnionFind(newParent, newSize, N);
    }

    // --- GETTERS ---
    public int getVirtualTop()    { return VIRTUAL_TOP; }
    public int getVirtualBottom() { return VIRTUAL_BOTTOM; }
    public int getVirtualLeft()   { return VIRTUAL_LEFT; }
    public int getVirtualRight()  { return VIRTUAL_RIGHT; }
}