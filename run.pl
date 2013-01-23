#!/usr/bin/perl

@canfiles = ("arrayCopy", "fibo", "loadsFPAddsStores",  "multiply",
"branch1",    "fiboLoop",           "loadsFPDivsStores",   "powers",
"branch2",    "loadsDaddiStores",   "loadsFPMultsStores",  "vectorDiv",
"daddiu" ,    "loadsDaddiuStores",  "loadsStores1",        "vectorSub",
"dump",       "loadsDaddsStores",   "loadsStores2",
"factorial",  "loadsDsubsStores",   "matrixMult");

$candir = "/u/css/classes/5483/124/TS/";

$dir = "Tests/";

if (! -e $dir)
{
   print "need to create a Tests directory first\n";
   exit();
}
system "rm -f Tests/*";                                                                                
#You may need to change the class name in quotes below to
#look for your main class if it isn't Tomasulo.class
if (! -e "bin/Tomasulo.class")
{
   print "missing simulator executable\n";
   exit();
}

$pass = 0;

for ($i = 0; $i <= $#canfiles; $i++){
   $prefix = $canfiles[$i];
   $input = $prefix.".hex";
   $output = $prefix.".output";
   $caninput = $candir.$prefix.".hex";
   $canoutput = $candir.$prefix.".output";
   system "cp $caninput .";
   print "Testing $input. ";
#  You may need to change the command in quotes below to invoke your Simulator
   system "java -cp bin Tomasulo $input > $output";
   #print "Comparing $output and $canoutput\n";
   # if output exists, compare to instructor's output
   if (-e $output)
   {
       system "diff $output $canoutput > $prefix.problems";
   }
#  does output exist?
   if (! -e $output || ! system "test -s $prefix.problems"){
         #print "problems found in $output, keeping all temp files.\n";
         print " Failed.\n";
         system "mv $input Tests/";
         system "mv $output Tests/";
         system "mv $prefix.problems Tests/";
   } else {
         #print "No problems found removing all temp files.\n";
         system "rm -rf $output $input $prefix.problems";
         print " Passed.\n";
         $pass = $pass + 1;
   }
}

$total = $#canfiles + 1;
print "\n$pass out of $total passed.\n";
if ($pass != $total) {
   print "See Tests directory for failed tests\n";
}
