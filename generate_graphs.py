import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import glob
from pathlib import Path
import numpy as np

sns.set_theme(style="whitegrid")
sns.set_context("talk")

CSV_ROOT = Path("csv")
OUTPUT_DIR = Path("plots")
HEAT_PUT = Path("heatmap")
OUTPUT_DIR.mkdir(exist_ok=True)
HEAT_PUT.mkdir(exist_ok=True)


ALLOWED_BASE_BUDGETS = {
    5:  [10, 50, 100, 500, 1000, 2000],
    8:  [10, 50, 100, 500, 1000, 2000],
    10: [10, 50, 100, 400, 800, 1600],
    14: [500, 1000, 1500, 2500, 3500],
}
RATIO_ORDER = ["1:1", "1:2", "1:5", "2:1", "5:1"]

def load_and_prepare_global_data():
    files = glob.glob("csv/*/*.csv")
    dfs = []
    for f in files:
        try:
            df = pd.read_csv(f)
            if not df.empty and 'games' in df.columns:
                dfs.append(df)
        except Exception as e:
            print(f"Erreur avec {f}: {e}")
            
    df = pd.concat(dfs, ignore_index=True)

    def extract_all_stats(row):
        p1_color = row['start_color']
        
        # Identifier P1 (Bleu) et P2 (Rouge)
        if row['winner'] == p1_color:
            p1_algo, p1_budget, p1_wins = row['algo_winner'], row['budget_winner'], row['win_count']
            p2_algo, p2_budget = row['algo_loser'], row['budget_loser']
        else:
            p1_algo, p1_budget, p1_wins = row['algo_loser'], row['budget_loser'], row['loss_count']
            p2_algo, p2_budget = row['algo_winner'], row['budget_winner']
            
        p1_win_rate = (p1_wins / row['games']) * 100
        
        # Isoler le taux de RAVE contre MCTS
        rave_win_rate = None
        if p1_algo == 'rave' and p2_algo == 'mcts':
            rave_win_rate = p1_win_rate
        elif p2_algo == 'rave' and p1_algo == 'mcts':
            rave_win_rate = 100 - p1_win_rate
            
        return pd.Series([p1_algo, p2_algo, int(p1_budget), int(p2_budget), p1_win_rate, rave_win_rate])

    df[['p1_algo', 'p2_algo', 'budget_p1', 'budget_p2', 'p1_win_rate', 'rave_win_rate']] = df.apply(extract_all_stats, axis=1)
    
    return df



def plot_rave_efficiency(df):
    df_rave_mcts = df[df['rave_win_rate'].notna() & (df['ratio'] == '1:1')].copy()
    df_rave_mcts = df_rave_mcts.groupby(['budget_p1', 'size'])['rave_win_rate'].mean().reset_index()

    plt.figure(figsize=(12, 7))
    sns.barplot(data=df_rave_mcts, x='budget_p1', y='rave_win_rate', hue='size', palette='viridis', edgecolor='black')
    plt.axhline(50, color='red', linestyle='--', linewidth=2.5, label='Équité (50%)')
    plt.title("Efficacité de RAVE face à MCTS (Budget égal - Ratio 1:1)", fontsize=16, fontweight='bold', pad=20)
    plt.xlabel("Budget d'exploration", fontsize=14, fontweight='bold')
    plt.ylabel("Taux de victoire de RAVE (%)", fontsize=14, fontweight='bold')
    plt.ylim(0, 105)
    plt.legend(title='Taille', bbox_to_anchor=(1.02, 1), loc='upper left')
    plt.savefig(OUTPUT_DIR / '1_efficacite_rave_mcts.png', dpi=300, bbox_inches='tight')
    plt.close()

def plot_first_player_advantage(df_miroir):
    df_plot = df_miroir[df_miroir['ratio'] == '1:1'].groupby(['budget_p1', 'size', 'p1_algo'])['p1_win_rate'].mean().reset_index()

    plt.figure(figsize=(12, 7))
    sns.lineplot(data=df_plot, x='budget_p1', y='p1_win_rate', hue='size', style='p1_algo', markers=True, dashes=False, linewidth=2.5, palette='Set1', markersize=10)
    
    plt.axhline(50, color='red', linestyle='--', linewidth=2.5, label='Équité (50%)')
    plt.title("Avantage du Premier Joueur (Matchs symétriques à ratio 1:1)", fontsize=16, fontweight='bold', pad=20)
    plt.xlabel("Budget d'exploration (Simulations)", fontsize=14, fontweight='bold')
    plt.ylabel("Taux de victoire du Joueur 1 (%)", fontsize=14, fontweight='bold')
    plt.ylim(0, 105)
    
    plt.xscale('log')
    budgets = sorted(df_plot['budget_p1'].unique())
    
    plt.xticks(budgets, labels=[str(b) for b in budgets], rotation=45, ha='right')
    
    plt.legend(title='Taille & Algo', bbox_to_anchor=(1.02, 1), loc='upper left')
    plt.savefig(OUTPUT_DIR / '2_avantage_premier_joueur.png', dpi=300, bbox_inches='tight')
    plt.close()
    

def plot_equity_threshold(df_miroir):
    df_equity = df_miroir[df_miroir['ratio'].isin(['1:1', '1:2', '1:5'])].copy()
    df_equity['p2_multiplier'] = df_equity['ratio'].map({'1:1': 1, '1:2': 2, '1:5': 5})
    df_plot = df_equity.groupby(['p2_multiplier', 'size', 'p1_algo'])['p1_win_rate'].mean().reset_index()

    plt.figure(figsize=(12, 7))
    sns.lineplot(data=df_plot, x='p2_multiplier', y='p1_win_rate', hue='size', style='p1_algo', markers=['o', 's'], dashes=False, linewidth=2.5, palette='Set1', markersize=12)
    plt.axhline(50, color='red', linestyle='--', linewidth=2.5, label='Équité (50%)')
    plt.title("Seuil d'équité : Impact du désavantage budgétaire sur le Joueur 1", fontsize=16, fontweight='bold', pad=20)
    plt.xlabel("Désavantage budgétaire du Joueur 1 (Ratio J1:J2)", fontsize=14, fontweight='bold')
    plt.ylabel("Taux de victoire du Joueur 1 (%)", fontsize=14, fontweight='bold')
    plt.xticks([1, 2, 5], labels=['Budget Égal\n(1:1)', 'Budget Moitié\n(1:2)', 'Budget 5x Moindre\n(1:5)'])
    plt.ylim(-5, 105)
    plt.legend(title='Taille & Algo', bbox_to_anchor=(1.02, 1), loc='upper left')
    plt.savefig(OUTPUT_DIR / '3_seuil_equite_p1.png', dpi=300, bbox_inches='tight')
    plt.close()

def plot_mcts_catchup_rave(df):
    # 1. Filtrer uniquement les affrontements croisés (MCTS vs RAVE ou RAVE vs MCTS)
    df_cross = df[((df['p1_algo'] == 'mcts') & (df['p2_algo'] == 'rave')) | 
                  ((df['p1_algo'] == 'rave') & (df['p2_algo'] == 'mcts'))].copy()
    
    if df_cross.empty:
        return

    # 2. Recalculer les stats du point de vue strict de MCTS
    df_cross['mcts_pos'] = df_cross.apply(lambda r: 'MCTS Joue 1er (Bleu)' if r['p1_algo'] == 'mcts' else 'MCTS Joue 2e (Rouge)', axis=1)
    df_cross['mcts_win_rate'] = df_cross.apply(lambda r: r['p1_win_rate'] if r['p1_algo'] == 'mcts' else 100 - r['p1_win_rate'], axis=1)
    df_cross['mcts_budget'] = df_cross.apply(lambda r: r['budget_p1'] if r['p1_algo'] == 'mcts' else r['budget_p2'], axis=1)
    df_cross['rave_budget'] = df_cross.apply(lambda r: r['budget_p2'] if r['p1_algo'] == 'mcts' else r['budget_p1'], axis=1)
    
    # Ratio (ex: 0.5 si MCTS a 2x moins de budget, 2.0 s'il en a 2x plus)
    df_cross['mcts_ratio'] = df_cross['mcts_budget'] / df_cross['rave_budget']

    # 3. Agréger les données (moyenne)
    df_plot = df_cross.groupby(['mcts_ratio', 'size', 'mcts_pos'])['mcts_win_rate'].mean().reset_index()

    # 4. Création du graphique
    plt.figure(figsize=(13, 8))
    sns.lineplot(
        data=df_plot, x='mcts_ratio', y='mcts_win_rate', hue='size', style='mcts_pos',
        markers=['o', 'X'], dashes=False, linewidth=3, palette='Set1', markersize=12
    )
    
    plt.axhline(50, color='red', linestyle='--', linewidth=2.5, label='Équité parfaite (50%)')
    plt.title("MCTS peut-il rattraper RAVE par la force de calcul ?\nTaux de victoire de MCTS selon son ratio budgétaire face à RAVE", 
              fontsize=16, fontweight='bold', pad=20)
    plt.xlabel("Budget alloué à MCTS (par rapport à RAVE)", fontsize=14, fontweight='bold')
    plt.ylabel("Taux de victoire de MCTS (%)", fontsize=14, fontweight='bold')
    
    plt.xscale('log')
    plt.xticks([0.2, 0.5, 1, 2, 5], 
               labels=['MCTS a 5x moins\n(1:5)', 'MCTS a 2x moins\n(1:2)', 'Budget Égal\n(1:1)', 'MCTS a 2x plus\n(2:1)', 'MCTS a 5x plus\n(5:1)'])
    plt.ylim(-5, 105)
    plt.legend(title='Taille & Position de MCTS', bbox_to_anchor=(1.02, 1), loc='upper left')
    
    plt.savefig(OUTPUT_DIR / '6_mcts_catchup_rave.png', dpi=300, bbox_inches='tight')
    plt.close()
def plot_cross_equity_absolute_budget(df_global):
    # 1. Filtre pour les affrontements croisés MCTS vs RAVE
    df_cross = df_global[((df_global['p1_algo'] == 'mcts') & (df_global['p2_algo'] == 'rave')) | 
                         ((df_global['p1_algo'] == 'rave') & (df_global['p2_algo'] == 'mcts'))].copy()

    # 2. Créer la légende des deux lignes
    df_cross['Ligne'] = df_cross.apply(
        lambda r: "MCTS (Bleu) vs RAVE (Rouge)" if r['p1_algo'] == 'mcts' else "RAVE (Bleu) vs MCTS (Rouge)", 
        axis=1
    )

    # 3. Dessin
    plt.figure(figsize=(12, 7))

    # On utilise budget_p1 sur l'axe X au lieu du ratio
    sns.lineplot(
        data=df_cross, 
        x='budget_p1', 
        y='p1_win_rate', 
        hue='Ligne', 
        style='Ligne',
        markers=['o', 's'], 
        dashes=False, 
        linewidth=3, 
        palette=['#1f77b4', '#d62728'], # Bleu pour MCTS Bleu, Rouge pour RAVE Bleu
        markersize=12
    )

    plt.axhline(50, color='black', linestyle='--', linewidth=2.5, label='Équité parfaite (50%)')

    plt.title("Équité Croisée : MCTS vs RAVE\nLe taux de victoire du Joueur 1 (Bleu) selon son budget absolu", 
              fontsize=16, fontweight='bold', pad=20)
    plt.xlabel("Budget d'exploration du Joueur 1 (Bleu) en simulations", fontsize=14, fontweight='bold')
    plt.ylabel("Taux de victoire du Joueur 1 (Bleu) (%)", fontsize=14, fontweight='bold')

    # Échelle algorithmique et rotation des textes pour que ce soit lisible
    plt.xscale('log')
    budgets = sorted(df_cross['budget_p1'].unique())
    plt.xticks(budgets, labels=[str(b) for b in budgets], rotation=45, ha='right')
    plt.ylim(-5, 105)

    plt.legend(title='Scénario (Qui joue en premier ?)', bbox_to_anchor=(1.02, 1), loc='upper left')

    plt.savefig(OUTPUT_DIR / '7_equite_croisee_budget.png', dpi=300, bbox_inches='tight')
    plt.close()

def plot_variance_boxplot(df_miroir):
    ratios_to_keep = ['1:5', '1:2', '1:1', '2:1', '5:1']
    df_box = df_miroir[df_miroir['ratio'].isin(ratios_to_keep)]

    plt.figure(figsize=(12, 8))
    sns.boxplot(data=df_box, x='ratio', y='p1_win_rate', order=ratios_to_keep, palette="coolwarm", showmeans=True, meanprops={"marker":"D", "markerfacecolor":"white", "markeredgecolor":"black", "markersize":8})
    sns.swarmplot(data=df_box, x='ratio', y='p1_win_rate', order=ratios_to_keep, color="black", alpha=0.6, size=6)
    plt.axhline(50, color='red', linestyle='--', linewidth=2.5, label='Équité parfaite (50%)')
    plt.title("Dispersion du Taux de Victoire du Joueur 1 selon le Ratio Budgétaire", fontsize=16, fontweight='bold', pad=20)
    plt.xlabel("Ratio Budgétaire (Joueur 1 : Joueur 2)", fontsize=14, fontweight='bold')
    plt.ylabel("Taux de victoire du Joueur 1 (%)", fontsize=14, fontweight='bold')
    plt.ylim(-5, 105)
    plt.xticks(ticks=range(5), labels=["Désavantage fort\n(1:5)", "Désavantage\n(1:2)", "Budget Égal\n(1:1)", "Avantage\n(2:1)", "Avantage fort\n(5:1)"])
    plt.legend(loc='upper left')
    plt.savefig(OUTPUT_DIR / '4_boxplot_ratio_variance.png', dpi=300, bbox_inches='tight')
    plt.close()

def plot_global_heatmap(df_miroir):

    df_agg = df_miroir.groupby(['budget_p1', 'budget_p2'])['p1_win_rate'].mean().reset_index()
    pivot_table = df_agg.pivot(index='budget_p1', columns='budget_p2', values='p1_win_rate').sort_index(ascending=False).sort_index(axis=1, ascending=True)

    plt.figure(figsize=(14, 10))
    cmap = sns.diverging_palette(10, 240, as_cmap=True, center="light")
    sns.heatmap(pivot_table, annot=True, fmt=".0f", cmap=cmap, center=50, vmin=0, vmax=100, cbar_kws={'label': 'Taux de victoire du Joueur 1 (%)'}, linewidths=.5, linecolor='gray', annot_kws={"size": 11})
    plt.title("Carte de Chaleur Globale : Influence croisée des budgets", fontsize=18, fontweight='bold', pad=20)
    plt.ylabel("Budget P1 (Bleu)", fontsize=16, fontweight='bold')
    plt.xlabel("Budget P2 (Rouge)", fontsize=16, fontweight='bold')
    plt.tight_layout()
    plt.savefig(OUTPUT_DIR / '5_heatmap_budget_global.png', dpi=300)
    plt.close()

def parse_ratio(ratio):
    left, right = str(ratio).split(":")
    return int(left), int(right)

def compute_metrics(df):
    df = df.copy()
    df.columns = [c.strip() for c in df.columns]
    df[["blue_ratio", "red_ratio"]] = df["ratio"].apply(lambda r: pd.Series(parse_ratio(r)))
    df["blue_budget"] = df.apply(lambda row: row["budget_winner"] if row["winner"] == "BLUE" else row["budget_loser"], axis=1)
    df["red_budget"] = df.apply(lambda row: row["budget_winner"] if row["winner"] == "RED" else row["budget_loser"], axis=1)
    df["blue_wins"] = df.apply(lambda row: row["win_count"] if row["winner"] == "BLUE" else row["loss_count"], axis=1)
    df["blue_winrate"] = 100 * df["blue_wins"] / df["games"]
    df["red_winrate"] = 100 - df["blue_winrate"]
    df["base_budget"] = (df["blue_budget"] / df["blue_ratio"]).round().astype(int)
    return df

def plot_ratio_curves_by_size(df_global):
    OUT_DIR = Path('courbes')
    OUT_DIR.mkdir(exist_ok=True)
    
    # === RECRÉER LE MATCHUP ===
    df = df_global.copy()
    # On reconstruit le nom du matchup à partir des algos
    df['matchup'] = df['p1_algo'] + 'VS' + df['p2_algo']
    
    # === STYLE ET CONFIG ===
    BG='white'; PANEL='white'; TEXT='black'; MUTED='black'; GRID='#dddddd'
    
    MATCHUP_CFG = {
        'mctsVSmcts': 'MCTS vs MCTS',
        'raveVSrave': 'RAVE vs RAVE',
        'raveVSmcts': 'RAVE (Bleu) vs MCTS (Rouge)', # Correction de la majuscule ici
        'mctsVSrave': 'MCTS (Bleu) vs RAVE (Rouge)',
    }
    
    RATIO_ORDER  = ['1:1','1:2','1:5','2:1','5:1']
    
    RATIO_CFG = {
        '1:1': dict(color='#e05858', marker='v', lw=2.2, ms=8, label='1:1'),
        '1:2': dict(color='#e8904a', marker='s', lw=2.2, ms=8, label='1:2'),
        '1:5': dict(color='#c0a0e8', marker='D', lw=2.2, ms=8, label='1:5'),
        '2:1': dict(color='#50c8f0', marker='o', lw=2.2, ms=8, label='2:1'),
        '5:1': dict(color='#4ea6e8', marker='^', lw=2.2, ms=8, label='5:1'),
    }

    # Calcul robuste du "budget de base" pour l'axe X
    def calc_base(row):
        try:
            r1, r2 = map(int, str(row['ratio']).split(':'))
            return int(row['budget_p1'] / r1)
        except:
            return int(row['budget_p1'])
            
    df['base_budget'] = df.apply(calc_base, axis=1)

    # === PLOTS ===
    for matchup, matchup_label in MATCHUP_CFG.items():
        df_m   = df[df['matchup']==matchup]
        if df_m.empty: continue
        
        sizes  = sorted(df_m['size'].unique())
        ratios = [r for r in RATIO_ORDER if r in df_m['ratio'].unique()]

        for size in sizes:
            df_ms  = df_m[df_m['size']==size].copy()
            if df_ms.empty: continue

            fig, ax = plt.subplots(figsize=(12, 7), facecolor=BG)
            ax.set_facecolor(PANEL)

            # Zones BLUE / RED
            ax.axhspan(0,  50, color='#e05858', alpha=0.07)
            ax.axhspan(50,100, color='#4ea6e8', alpha=0.07)
            ax.axhline(50, color='black', lw=1.2, ls='--', alpha=0.6)

            # === ANCRAGE SÉCURISÉ DE L'AXE X ===
            unique_base_budgets = sorted(df_ms['base_budget'].unique())
            budget_to_x = {b: i for i, b in enumerate(unique_base_budgets)}

            # === COURBES ===
            for ratio in ratios:
                rcfg = RATIO_CFG.get(ratio)
                if not rcfg: continue
                
                dm = df_ms[df_ms['ratio']==ratio].sort_values('base_budget')
                if dm.empty: continue

                # On utilise l'ancrage pour être sûr que le point est au bon endroit
                xs = [budget_to_x[b] for b in dm['base_budget']]
                ys = dm['p1_win_rate'].values

                ax.plot(xs, ys,
                        color=rcfg['color'],
                        marker=rcfg['marker'],
                        lw=rcfg['lw'],
                        ms=rcfg['ms'],
                        label=rcfg['label'])

                # annotations % (légèrement surélevées pour ne pas masquer le point)
                for xi, y in zip(xs, ys):
                    ax.text(xi, y + 2.5, f'{y:.0f}%',
                            ha='center', va='bottom', fontsize=9, 
                            fontweight='bold', color=rcfg['color'])

            # === AXE X = BUDGET DE BASE ===
            ax.set_xticks(range(len(unique_base_budgets)))
            ax.set_xticklabels(unique_base_budgets, fontsize=11, fontweight='bold')
            ax.set_xlim(-0.5, len(unique_base_budgets) - 0.5)

            # === STYLE AXES ===
            ax.set_ylim(0, 115) # Légèrement augmenté pour laisser de la place au texte
            ax.set_yticks([0, 25, 50, 75, 100])
            ax.yaxis.set_major_formatter(plt.FuncFormatter(lambda v,_: f'{v:.0f}%'))

            ax.set_title(f'{matchup_label} — Grille {size}×{size}',
                         fontsize=16, fontweight='bold', color=TEXT, pad=15)

            ax.set_xlabel('Budget de base de l\'IA', fontsize=14, fontweight='bold', color=MUTED)
            ax.set_ylabel('Taux de victoire du Joueur 1 (Bleu) (%)', fontsize=14, fontweight='bold', color=MUTED)

            ax.grid(axis='y', color=GRID)
            
            # Légende placée à l'extérieur pour ne pas cacher les lignes !
            ax.legend(title='Ratio (BLEU:ROUGE)', bbox_to_anchor=(1.02, 1), loc='upper left')

            # === SAVE ===
            out = OUT_DIR / f'{matchup}_size{size}.png'
            plt.tight_layout()
            plt.savefig(out, dpi=300, bbox_inches='tight') # dpi 300 et bbox_inches strict
            print(f'✅ Généré : {out}')
            plt.close()

def plot_individual_heatmap(df, size, duel_name):
    allowed = ALLOWED_BASE_BUDGETS.get(size, [])
    if not allowed:
        return

    df = df[df["base_budget"].isin(allowed)].copy()
    if df.empty:
        return

    df["label"] = df.apply(lambda row: f'{row["blue_winrate"]:.0f} / {row["red_winrate"]:.0f}', axis=1)
    pivot_val = df.pivot_table(index="ratio", columns="base_budget", values="blue_winrate", aggfunc="mean").reindex(index=RATIO_ORDER, columns=allowed)
    pivot_lab = df.pivot_table(index="ratio", columns="base_budget", values="label", aggfunc="first").reindex(index=RATIO_ORDER, columns=allowed)

    plt.figure(figsize=(12, 7))
    cmap = sns.diverging_palette(10, 240, as_cmap=True, center="light")
    sns.heatmap(pivot_val, annot=pivot_lab, fmt="", cmap=cmap, center=50, vmin=0, vmax=100, cbar_kws={'label': 'Taux de victoire P1 (%)'}, linewidths=.5, linecolor='gray', annot_kws={"size": 11, "weight": "bold"})
    plt.title(f"{duel_name} — Taille {size}", fontsize=18, fontweight='bold', pad=20)
    plt.xlabel("Budget de base", fontsize=16, fontweight='bold')
    plt.ylabel("Ratio (Bleu : Rouge)", fontsize=16, fontweight='bold')
    plt.tight_layout()
    plt.savefig(HEAT_PUT / f"h_{duel_name}_size{size}.png", dpi=300, bbox_inches="tight")
    plt.close()


def main():
    print("Démarrage du traitement...")

    print("Génération des graphiques globaux...")
    df_global = load_and_prepare_global_data()
    if not df_global.empty:
        df_miroir = df_global[df_global['p1_algo'] == df_global['p2_algo']].copy()
        
        plot_rave_efficiency(df_global)
        plot_first_player_advantage(df_miroir)
        plot_equity_threshold(df_miroir)
        plot_variance_boxplot(df_miroir)
        plot_global_heatmap(df_miroir)
        plot_mcts_catchup_rave(df_global)
        plot_cross_equity_absolute_budget(df_global)
        plot_ratio_curves_by_size(df_global)
    else:
        print("Aucune donnée globale trouvée.")

    print("Génération des heatmaps individuelles...")
    csv_files = [f for f in sorted(CSV_ROOT.glob("*/*.csv")) if not f.name.endswith(":Zone.Identifier")]
    
    for file in csv_files:
        try:
            df_indiv = pd.read_csv(file)
            if not df_indiv.empty:
                df_indiv = compute_metrics(df_indiv)
                size = int(df_indiv["size"].iloc[0])
                duel_name = file.parent.name
                plot_individual_heatmap(df_indiv, size, duel_name)
        except Exception as e:
            print(f"Erreur avec le fichier {file}: {e}")

    print(f"Terminé ! Tous les graphiques sont sauvegardés dans le dossier '{OUTPUT_DIR}'.")

if __name__ == "__main__":
    main()
