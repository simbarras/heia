import pokemon
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
import time

k_nb_td_max: int = 15
k_nb_td_min: int = 1
k_step = 1
args_ = {
    "min_pokemon": 1,
    "max_pokemon": 898,
    "tp": "T",
    "nbr_tp": 4,
    "path": "./tmp"
}
result_ = {
    "T": [],
    "T_key": [],
    "P": [],
    "P_key": [],
    "A_load": [],
    "Mi_load": [],
    "Ma_load": [],
}
color_ = {
    "T": "b",
    "P": "r"
}

def speed_thread( nb: int = k_nb_td_max):
    global result_
    args_['tp'] = "T"
    for i in range(k_nb_td_min, nb, k_step):
        args_['nbr_tp'] = i
        start = int(round(time.time()))
        results_load = pokemon.main(args_, True)
        end = int(round(time.time()))
        print(str(i) + " threads -> "+str(end- start) +" [s]")
        result_['T'].append(end - start)
        result_['T_key'].append(i)
        sum = 0
        min = results_load[0]
        max = results_load[0]
        for val in results_load:
            sum += val
            if min > val: min = val
            if max < val : max = val
        sum /= len(results_load)
        result_['A_load'].insert(i, sum)
        result_['Mi_load'].insert(i, min)
        result_['Ma_load'].insert(i, max)
    
    fig, (ax1, ax2) = plt.subplots(2, 1)
    fig.suptitle('Speed test threads')

    ax1.plot(result_['T_key'], result_['T'], c=color_['T'], label='Threads')
        
    ax1.set_ylabel('Time (s)')
    ax1.set_xlabel('Number of threads')

    ax2.plot(result_['T_key'], result_['A_load'], c='b', label='Load average')
    ax2.plot(result_['T_key'], result_['Ma_load'], 'g--', label='Max load')
    ax2.plot(result_['T_key'], result_['Mi_load'], 'r--', label='Min load')
        
    ax2.set_ylabel('Image by thread average')
    ax2.set_xlabel('Number of threads')
    return ax1, ax2

def speed_process( nb: int = k_nb_td_max):
    global result_
    args_['tp'] = "P"
    for i in range(k_nb_td_min, nb, k_step):
        args_['nbr_tp'] = i
        start = int(round(time.time()))
        results_load = pokemon.main(args_, True)
        end = int(round(time.time()))
        print(str(i) + " process -> "+str(end- start) +" [s]")
        result_['P'].append(end - start)
        result_['P_key'].append(i)
    
    fig, (ax1) = plt.subplots(1, 1)
    fig.suptitle('Speed test process')

    ax1.plot(result_['P_key'], result_['P'], c=color_['P'], label='Process')
        
    ax1.set_ylabel('Time (s)')
    ax1.set_xlabel('Number of process')
    return ax1

def load_experiment(type: str, number_of_tp: int = 4):
    args_["tp"] = type
    args_["nbr_tp"] = number_of_tp
    results = pokemon.main(args_, True)
    somme = 0
    for i in range(len(results)):
        somme += results[i]

    plt.title("Load by " + type)
    plt.ylabel("Number of images")
    plt.xlabel("Id")
    id = 0
    # for nb in results:
    #    plot.bar(id, somme / nb, str(results))
    #    id += 1
    plt.bar(range(len(results)), results)


if __name__ == '__main__':
    print("Speed test with threads...")
    speed_thread()
    #plt.show()
    print("Speed test with process...")
    speed_process()
    plt.show()
    print("Load test with threads...")
    #load_experiment("T", )
    #plt.show()
    print("Load test with process...")
    #load_experiment("P", )
    #plt.show()
