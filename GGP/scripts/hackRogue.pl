#!/usr/bin/perl

our $file = $ARGV[0];
if (not -e $file) {
  die "first arg is file\n";
}

$makeStatic = "./makeStatic.pl $file";

print `echo "source rogue-common.soar" >> $file`;
print `$makeStatic between`;
print `$makeStatic min`;
print `$makeStatic ncell`;
print `$makeStatic diff`;
print `$makeStatic adjacent`;
print `$makeStatic cardinalrelation`;
print `$makeStatic roomlocation`;
print `$makeStatic traversible`;
print `$makeStatic greaterthan`;
print `$makeStatic gtequal`;
print `$makeStatic plus`;
print `$makeStatic minus`;
