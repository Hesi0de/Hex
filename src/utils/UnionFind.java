package utils;

/**
 * Implémentation de la structure de données Union-Find (ou Disjoint Set Union) pour gérer les connexions dans un jeu de Hex.
 */
public class UnionFind {
    /**
     * Tableau des parents pour chaque élément. parent[i] est le parent de l'élément i.
     */
    private int[] parent;

    /**
     * Tableau des tailles pour chaque arbre. 
     */
    private int[] size;

    /**
     * Nombre de cases du plateau (N*N pour un plateau de taille N, ex : 196 pour 14*14).
     */
    private final int N; 
    
    // noeuds virtuels
    /**
     * Indices des noeuds virtuels pour les bords du plateau. Ces noeuds sont utilisés pour vérifier rapidement les connexions entre les bords.
     */
    private final int VIRTUAL_TOP;
    private final int VIRTUAL_BOTTOM;
    private final int VIRTUAL_LEFT;
    private final int VIRTUAL_RIGHT;


    /**
     * Constructeur de la classe UnionFind. Initialise les tableaux parent et size, ainsi que les indices des noeuds virtuels.
     * @param boardSize La taille du plateau de jeu.
     */
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

    // 
    /**
     * constructeur privé -> copie mcts => ne pas boucler en plus 
     * @param parent 
     * @param size 
     * @param n
     */
    private UnionFind(int[] parent, int[] size, int n) {
        this.parent = parent;
        this.size = size;
        this.N = n;
        this.VIRTUAL_TOP    = N;
        this.VIRTUAL_BOTTOM = N + 1;
        this.VIRTUAL_LEFT   = N + 2;
        this.VIRTUAL_RIGHT  = N + 3;
    }


    /**
     * Trouve la racine de l'élément p et effectue la compression de chemin pour optimiser les futures recherches.
     * @param p L'index de l'élément pour lequel trouver la racine.
     * @return L'index de la racine de l'élément p.
     */
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

    /**
     * Unit la composante contenant l'élément p avec celle contenant l'élément q.
     *  Utilise la technique d'union par taille pour maintenir les arbres équilibrés.
     * @param p L'index de l'élément p.
     * @param q L'index de l'élément q.
     */
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

    /**
     * Vérifie si les éléments p et q sont dans la même composante connectée, c'est-à-dire s'ils ont la même racine.
     * @param p L'index de l'élément p.
     * @param q L'index de l'élément q.
     * @return true si p et q sont connectés, false sinon.
     */
    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }

    //
    /**
     * Crée une copie pour mcts.
     * @return Une nouvelle instance de UnionFind avec les mêmes connexions que l'instance actuelle.
     */
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