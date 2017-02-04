import sys

if __name__ == '__main__':
    # We start by getting arguments: whether we want a sphere or a ball, and what dimension we want.
    if(len(sys.argv)<3):
        print("Usage:\npython3 generate_d_sphere_ball.py <sphere/ball> <d>.\nFor instance to generate the 2-sphere type \"python3 generate_d_sphere_ball.py sphere 2\"")
        quit()
    if(sys.argv[1]=="sphere"):
        sphere = True
    elif(sys.argv[1]=="ball"):
        sphere = False
    else:
        print("Usage:\npython3 generate_d_sphere_ball.py <sphere/ball> <d>.\nFor instance to generate the 2-sphere type \"python3 generate_d_sphere_ball.py sphere 2\"")
        quit()
    if(sys.argv[2].isdigit()):
        d = int(sys.argv[2])
    else:
        print("Usage:\npython3 generate_d_sphere_ball.py <sphere/ball> <d>.\nFor instance to generate the 2-sphere type \"python3 generate_d_sphere_ball.py sphere 2\"")
        quit()
    # Then we build the simplicial complex from the singleton 0 to the d-ball.
    scomplex = [[0]]
    for i in range(1,d+1): # to move from the i-1 ball to the i-ball, we add a point i and "connect" it to every past simplex
        buff = [[i]]
        for s in scomplex:
            copy = [n for n in s]
            copy.append(i)
            buff.append(copy)
        scomplex += buff
    # if we only want the sphere, we remove the interior, which is the last thing we added
    if(sphere):
        scomplex = scomplex[:-1]
    # we then write to a file with a trivial filtration
    f = open(str(d)+"-"+sys.argv[1]+".txt",'w')
    i = 0.0
    for s in scomplex:
        f.write(str(i)+" "+str(len(s)-1)+" "+(" ".join([str(n) for n in s]))+"\n")
        i += 1.0
    f.close()
