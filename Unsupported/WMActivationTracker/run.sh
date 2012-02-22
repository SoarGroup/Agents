#/bin/bash

CYCLES=$1

./gen_wma $CYCLES
php aggregate_wma.php $CYCLES > wma.csv
rm *.txt
