#!/usr/bin/python

import subprocess
import sys
import os
import filecmp
import re
from signal import alarm, signal, SIGALRM

class Suite:

    def __init__(self):
        self.groups = {}
        self.suiteScore = 0
        self.suiteMax = 0

    def processSuite(self):
        sorted_groups = [x for x in self.groups]
        sorted_groups.sort()
        for key in sorted_groups:
            self.groups[key].processGroup()
            self.suiteScore += self.groups[key].score
            self.suiteMax += self.groups[key].maxScore

    def printSummary(self):
        sorted_groups = [x for x in self.groups]
        sorted_groups.sort()
        for name in sorted_groups:
            self.groups[name].printSummary()
        print '------------Suite Overall----------'
        print 'Max Score: {0}'.format(self.suiteMax)
        print 'Score: {0}'.format(self.suiteScore)

class TestGroup:

    def __init__(self, name):
        self.name = name
        self.tests = []
        self.maxScore = 0
        self.score = 0

    def addTest(self, test):
        self.tests.append(test)

    def processGroup(self):
        sorted_tests = [x for x in self.tests]
        sorted_tests.sort()
        for test in sorted_tests:
            try:
                num = int(test.config['weight'])
            except ValueError:
                num = float(test.config['weight'])
            if test.status:
                self.score += num
            self.maxScore += num
            test.printSummary()

    def printSummary(self):
        print '#############Suite: {0} #################'.format(self.name)
        print 'Max Score: {0}'.format(self.maxScore)
        print 'Score: {0}'.format(self.score)


class Test:

    def __init__(self, name):
        self.name = name
        self.testFile = testDir + '/' + name + '.test'
        self.solutionFile = solDir + '/' + name + '.sol'
        self.answerFile = ansDir + '/' + name + '.ans'
        self.configFile = testDir + '/' + name + '.config'
        self.status = None
        self.summaryString = ''
        ######Configs########
        self.config = {}
        self.config['group'] = 'default'
        self.config['weight'] = '1'
        self.config['check'] = 'diff'

    def parseTestConfig(self):

        if os.path.isfile(self.configFile):
            configs = CleanFile(self.configFile)
            for line in configs:
                line = line.replace(' ', '')
                split = line.split('=')
                key = split[0]
                value = split[1]
                self.config[key] = value

    def runTest(self, runString):

        print 'Running {0}'.format(self.testFile)
        with open(self.testFile, 'r') as test:
            testLines = [ line.rstrip() for line in test.readlines() ]

        ExecuteCommandWithTee(runString, inputLines=testLines, outputFile=self.answerFile)

        print '------------------------------------------------------------------'

#TODO: Support consistent log check
    def fileDiff(self):

        if filecmp.cmp(self.solutionFile, self.answerFile):
            self.status = True
            return

        solution = CleanFile(self.solutionFile)
        answer = CleanFile(self.answerFile)

        #Doesn't respect order.....
        missing = [ line for line in solution if not line in answer ]
        added = [ line for line in answer if not line in solution ]

        if not missing and not added:
            self.summaryString += 'Content is the same, had to clean the output though\n'
            self.status = True
            return

        for line in missing:
            self.summaryString += 'Missed line: {0} \n'.format(line)

        for line in added:
            self.summaryString += 'Added line: {0} \n'.format(line)

        self.status = False
        return

    def snapshotDiff(self):

        solution = CleanFile(self.solutionFile)
        answer = CleanFile(self.answerFile)
        consistencyMap = {}
        self.status = True

        if not len(solution) == len(answer):
            if (len(solution) > len(answer)):
               self.summaryString += 'Too little lines in the answer\n'
            else:
                self.summaryString += 'Too many line in the answer\n'
            self.status = False
            return
        for i in range(len(solution)):
            sol = solution[i].split(', ')
            ans = answer[i].split(', ')

            for j in range(len(sol)):
                if re.match('\{.*\}', sol[j]):
                        key = sol[j][1:-1]
                        if key in consistencyMap.keys():
                                if consistencyMap[key] != ans[j]:
                                        self.summaryString += 'Line {0} is inconsistent\n'.format(str(i))
                                        self.status = False
                                        continue
                                else:
                                        continue
                        else:
                                consistencyMap[key] = ans[j]
                                continue
                elif not sol[j] == '*':
                    if not sol[j] == ans[j]:
                        self.summaryString += 'Line {0} is incorrect\n'.format(str(i))
                        self.status = False
                        continue
        return

    def printSummary(self):
        print '******* Test: {0} *********'.format(self.name)
        print 'Config: {0}'.format(self.config)
        print 'Status : {0}'.format('Pass' if self.status else 'Fail')
        print self.summaryString.rstrip('\n')
        print '***************************'


testDir = 'tests'
ansDir = 'answers'
solDir = 'solutions'
runString = ''

def ExecuteCommand(cmdStr):

    return [line.rstrip('\n') for line in iter(subprocess.Popen(cmdStr, shell=True, stdout=subprocess.PIPE).stdout.readline, '')]

def ExecuteCommandWithTee(cmdStr, inputLines, outputFile):

    assert not outputFile is None
    FNULL = open(os.devnull, 'w')
    '''
    Run a command with a timeout after which it will be forcibly
    killed.
    '''
    class Alarm(Exception):
        pass
    def alarm_handler(signum, frame):
        raise Alarm
    signal(SIGALRM, alarm_handler)
    alarm(300)
    try:
        proc = subprocess.Popen(cmdStr, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=FNULL)
        out, err = proc.communicate(input='\n'.join(inputLines))
        alarm(0)
        with open(outputFile, 'w') as outfile:
            print out.rstrip()
            outfile.write(out)
    except Alarm:
        print 'KILLING THE MOFO'
        proc.terminate()
        proc.kill()
        with open(outputFile, 'w') as outfile:
            outfile.write("")


def UserContinue(continueString):

    validAnswers = { '': True, 'y': True, 'ye': True, 'yes': True,
                     'n': False, 'no': False }

    while True:

        sys.stdout.write(continueString + '[Y] ')
        answer = raw_input().lower()

        if not answer in validAnswers.keys():
            print 'Please answer yes or no'
        else:
            return validAnswers[answer]

def CleanFile(fileName):

    with open(fileName, 'r') as file:
        fileList = [ line.strip() for line in file.readlines() ]

    return fileList

def GetTestList():
    return [file for file in os.listdir(testDir) if re.search('\.test$', file)]

def GetSolutionList():
    return [file for file in os.listdir(solDir) if re.search('\.sol$', file)]

def GetAnswerList():
    return [file for file in os.listdir(ansDir) if re.search('\.ans$', file)]

#TODO: Create arbitrary key value parser that will store in a dict


def PrepareEnvironment():

    runString = ExecuteCommand('cat COMMAND')
    assert len(runString) == 1

    if not UserContinue('Safe to run `{0}` ? '.format(runString[0])):
        exit(-1);

    testList = GetTestList()
    solutionList = GetSolutionList()

    #assert len(testList) == len(solutionList)
    for test in testList:
        assert test.replace('.test', '.sol') in solutionList

    rc = ExecuteCommand('rm -rf answers/')
    assert len(rc) == 0

    rc = ExecuteCommand('mkdir answers')
    assert len(rc) == 0

    return (runString[0], [ test.replace('.test', '') for test in testList ])



def RunTests(currentSuite, runString):

    files = [x for x in GetTestList()]
    files.sort()
    for testName in files:
        test = Test(testName.replace('.test', ''))
        test.parseTestConfig()
        test.runTest(runString)
        if (test.config['check'] == 'snapshot'):
            test.snapshotDiff()
        else:
            test.fileDiff()
        try:
            currentSuite.groups[test.config['group']].addTest(test)
        except KeyError:
            currentSuite.groups[test.config['group']] = TestGroup(test.config['group'])
            currentSuite.groups[test.config['group']].addTest(test)

def main():

    global testDir
    global ansDir
    global solDir
    global runString

    #Preparing environment
    (runString, testList) = PrepareEnvironment()

    currentSuite = Suite()

    RunTests(currentSuite, runString)

    currentSuite.processSuite()

    currentSuite.printSummary()

    print "Suite Done"

if __name__ == '__main__':
    main()

