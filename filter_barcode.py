import sys

if __name__ == '__main__':

    if(len(sys.argv)<3):
        print("Usage:\npython3 filter_barcode.py <file_base> <min>.\nFor instance to remove every bar of length less than 0.05 from filtration B: \"python3 filter_barcode.py filtration_B 0.05\"")
        quit()
    base = sys.argv[1]
    f = open("results/"+base+".out",'r')
    try:
        mini = float(sys.argv[2])
    except ValueError:
        print("Usage:\npython3 filter_barcode.py <file_base> <min>.\nFor instance to remove every bar of length less than 0.05 from filtration B: \"python3 filter_barcode.py filtration_B 0.05\"")
        quit()
    out = open("results/"+base+"_filtered.out",'w')
    for line in f:
        v = line.split(' ')
        start,end = float(v[1]),float(v[2])
        if(end-start>mini):
            out.write(line)
