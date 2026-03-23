#!/bin/bash

set -u

MAX_JOBS=2
GAMES=250

CLASS_PATH="build"
MAIN_CLASS="analysis.GameLauncher2"

RUN_NAME="mctsVSrave"
ALGO_BLUE="mcts"
ALGO_RED="rave"

START_COLOR="BLUE"

# Dossiers de sortie
TMP_DIR="tmp/$RUN_NAME"
OUT_DIR="csv/$RUN_NAME"

OUT5="$OUT_DIR/results_size5.csv"
OUT8="$OUT_DIR/results_size8.csv"
OUT10="$OUT_DIR/results_size10.csv"

RATIOS=("1:1" "1:2" "1:5" "2:1" "5:1")

# Budgets
BUDGETS_5=(10 50 100 500 1000 2000)
BUDGETS_8=(10 50 100 500 1000 2000)
BUDGETS_10=(10 50 100 400 800 1600)

# Préparation
mkdir -p "$TMP_DIR"
mkdir -p "$OUT_DIR"

rm -f "$TMP_DIR"/*.csv
rm -f "$OUT5" "$OUT8" "$OUT10"

HEADER="games,size,ratio,start_color,winner,win_count,budget_winner,algo_winner,loss_count,budget_loser,algo_loser"

echo "$HEADER" > "$OUT5"
echo "$HEADER" > "$OUT8"
echo "$HEADER" > "$OUT10"

running_jobs=0
job_id=0

# Vérification
if ! java -cp "$CLASS_PATH" "$MAIN_CLASS" 5 1 1:1 10 10 "$ALGO_RED" "$ALGO_BLUE" "$START_COLOR" >/dev/null 2>&1; then
    echo "Erreur: impossible de lancer $MAIN_CLASS"
    exit 1
fi

# Lancer une config
launch_job() {
    local size="$1"
    local ratio="$2"
    local base="$3"

    local r_blue="${ratio%%:*}"   # Bleu prend la première valeur du ratio
    local r_red="${ratio##*:}"    # Rouge prend la deuxième valeur du ratio

    local budget_blue=$((base * r_blue))
    local budget_red=$((base * r_red))

    local outfile
    outfile=$(printf "%s/size%s_job_%03d.csv" "$TMP_DIR" "$size" "$job_id")

    echo "Launch -> duel=$RUN_NAME size=$size ratio=$ratio base=$base starter=$START_COLOR blue=$budget_blue red=$budget_red"

    java -cp "$CLASS_PATH" "$MAIN_CLASS" \
        "$size" \
        "$GAMES" \
        "$ratio" \
        "$budget_blue" \
        "$budget_red" \
        "$ALGO_BLUE" \
        "$ALGO_RED" \
        "$START_COLOR" \
        > "$outfile" &

    ((running_jobs++))
    ((job_id++))

    if [ "$running_jobs" -ge "$MAX_JOBS" ]; then
        wait -n
        ((running_jobs--))
    fi
    
}

# Lancer toutes les configs d'une taille
run_size() {
    local size="$1"
    local outfile="$2"
    shift 2
    local budgets=("$@")

    for ratio in "${RATIOS[@]}"; do
        for base in "${budgets[@]}"; do
            launch_job "$size" "$ratio" "$base"
        done
    done

    wait
    running_jobs=0

    for f in "$TMP_DIR"/size${size}_job_*.csv; do
        [ -e "$f" ] && cat "$f" >> "$outfile"
    done

    rm -f "$TMP_DIR"/size${size}_job_*.csv

    echo "✔ Taille $size terminée -> $outfile"
}

# Exécution
run_size 5 "$OUT5" "${BUDGETS_5[@]}"
run_size 8 "$OUT8" "${BUDGETS_8[@]}"
run_size 10 "$OUT10" "${BUDGETS_10[@]}"

echo "✅ DONE"
echo "Duel : $RUN_NAME"
echo "Fichiers générés :"
echo "  $OUT5"
echo "  $OUT8"
echo "  $OUT10"