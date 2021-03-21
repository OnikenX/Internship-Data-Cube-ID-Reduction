public class DataCube {
    ShellFragment[] shellFragmentsList;


    /**
     * @param arrayOfValues d1 d1 d1 d1     //as diensões estao ao longo de linhas
     *                      d2 d2 d2 d2
     *                      d3 d3 d3 d3
     */
    public DataCube(int[][] arrayOfValues, int[] sizes) {
        shellFragmentsList = new ShellFragment[arrayOfValues[0].length];    //aloca memória para todas a dimensões
        int count = 1;
        for (int i = 0; i < arrayOfValues[i].length; i++) {       //para cada uma das linas (dimensões)

            int[] arr = new int[arrayOfValues.length];          //cria um array com tamanho das linhas
            for (int n = 0; n < arrayOfValues.length; n++)        //copia o vaor de cada linha para novo array
            {
                arr[n] = arrayOfValues[n][i];
            }

            shellFragmentsList[i] = new ShellFragment(arr, sizes[0]);              //cria shell fragment
            System.out.println("Dimension number " + count + " created");
            count++;
            System.gc();

        }

    }

    public DataCube(int[][] arrayOfValues) {
        shellFragmentsList = new ShellFragment[arrayOfValues[0].length];    //aloca memória para todas a dimensões

        for (int i = 0; i < arrayOfValues[i].length; i++) {       //para cada uma das linas (dimensões)
            int[] arr = new int[arrayOfValues.length];          //cria um array com tamanho das linhas
            for (int n = 0; n < arrayOfValues.length; n++)        //copia o vaor de cada linha para novo array
            {
                arr[n] = arrayOfValues[n][i];
            }
            shellFragmentsList[i] = new ShellFragment(arr);              //cria shell fragment

        }

    }

    public StringBuilder showDimensions() {
        StringBuilder str = new StringBuilder();
        int dimension = 1;
        for (ShellFragment shellFragment : shellFragmentsList) {
            str.append("Dimension number ").append(dimension).append("\n");
            for (int value : shellFragment.getValues()) {
                str.append("\nvalue ").append(value).append("\n");
                str.append("TID List\t");
                for (int i : shellFragment.getTIDListFromValue(value))
                    str.append(i).append(" ");
            }
            str.append("\n");
            dimension++;
        }
        return str;
    }

    /**
     * @param instanciations array of instanciated dimensions. the array must not have a greater lenght than the number of dimensions
     * @return int array with the IDs of the tuples that have the instantiated characteristics or NULL if the instanciated array has a bigger
     * lenght than the number of dimensions.
     */
    public int[] searchMultipleDimensionsAtOnce(int[] instanciations) {
        if (instanciations.length > shellFragmentsList.length)
            return null;
        int[] finalList = new int[0];
        boolean instanciated = false;

        for (int i = 0; i < instanciations.length; i++) {
            if (instanciations[i] != '*' && instanciations[i] != '?') {
                if (!instanciated) {  //caso nunca tenha havido uma instanciação até esta dimensão
                    finalList = shellFragmentsList[i].getTIDListFromValue(instanciations[i]);        //coloca os valores como valores iniciais
                    instanciated = true;
                    if (finalList == null)           //caso não haja valores, nao vale a pena continuar
                        return new int[0];
                } else {
                    int[] arr = shellFragmentsList[i].getTIDListFromValue(instanciations[i]);
                    if (arr == null)
                        return new int[0];
                    finalList = intersections(finalList, arr);      //intercepta valores
                    if (finalList.length == 0)           //caso não haja valores, nao vale a pena continuar
                        return finalList;
                }
            }
        }
        if (!instanciated) //caso nenhuma dimensão tenha sido instanciada
            return shellFragmentsList[0].getAllTIDs();

        return finalList;


    }

    /**
     * @param finalList        array n1
     * @param tidListFromValue array n2
     * @return returns an array that is the result of an mathematical intersection betweem array n1 and array n2.
     */
    private int[] intersections(int[] finalList, int[] tidListFromValue) {
        int[] retornable = new int[0];
        int[] secundary;
        for (int a : finalList)
            for (int b : tidListFromValue)
                if (a == b) {
                    secundary = new int[retornable.length + 1];
                    System.arraycopy(retornable, 0, secundary, 0, retornable.length);
                    secundary[secundary.length - 1] = a;
                    retornable = secundary;
                }
        return retornable;
    }


    /**
     * @param arrayOfValues array with the query values
     * @return An datacube with the tuples that respect the query values, or null, if there is tuples that have suck values
     */
    public DataCube getSubCube(int[] arrayOfValues) {
        int[] tidArrat = this.searchMultipleDimensionsAtOnce(arrayOfValues);            //obtem TIDs resultante
        if (tidArrat == null)
            return null;

        int[][] subCubeValues = new int[tidArrat.length][];                             //aloca memoria array de valores

        for (int i = 0; i < tidArrat.length; i++) {                                     //para cada um dos IDs de tuples que respeita o pedido
            subCubeValues[i] = getDimensions(tidArrat[i]);                              //obtem-se os seus valores e coloca-se no array de representação de objetos
        }
        return new DataCube(subCubeValues);                                             //retorna noivo cubo de dados

    }

    /**
     * @param index index value (used as ID) of the tuple/object
     * @return int[] array with the values of each dimension, if the index is not fdound, returns NULL
     */
    public int[] getDimensions(int index) {
        int[] result = new int[shellFragmentsList.length];                                  //aloca memoria para cada uma das diemns~ºoes

        for (int i = 0; i < shellFragmentsList.length; i++) {                                 //para cada uma das dimensões
            result[i] = shellFragmentsList[i].getValueFromID(index);                        //obtem valor da dimensão tendo em conta o index
            if (result[i] == -1)
                return null;
        }
        return result;
    }

    /**
     * @return number of shellfragments/ dimensions
     */
    public int getNumberShellFragments() {
        return shellFragmentsList.length;
    }

    public StringBuilder showIndividualTuples() {
        StringBuilder str = new StringBuilder();
        str.append("id:\t");
        for (int i = 0; i < shellFragmentsList.length; i++)
            str.append("D").append((i + 1)).append("\t");
        str.append("\n");

        for (int id : shellFragmentsList[0].getAllTIDs()) {
            str.append(id).append(":\t");
            for (ShellFragment shellFragment : shellFragmentsList) {
                str.append(shellFragment.getValueFromID(id)).append("\t");
            }
            str.append("\n");
        }
        return str;
    }

    public void showAllQueryPossibilities() {
        StringBuilder str = new StringBuilder();
        System.out.println(getShellFreagmentSize());

        for (int i = 0; i < shellFragmentsList.length; i++)
            str.append("D").append(i + 1).append("\t");
        str.append(":\tN\n");

        System.out.println(str);

        int[] indexList = new int[shellFragmentsList.length];
        for (int i : indexList)
            i = 0;

        do {
            str = new StringBuilder();
            int[] tuple = searchMultipleDimensionsAtOnce(indexList);

            for (int i : indexList)
                str.append(i).append("\t");
            str.append(":\t").append(tuple.length).append("\n");

            for (int i = indexList.length - 1; i >= 0; i--) {
                if (indexList[i] < getShellFreagmentSize()-1)
                {
                    indexList[i]++;
                    break;
                }
                else if(indexList[i] == getShellFreagmentSize())
                {
                    indexList[i] = '*';
                    break;
                }else
                    indexList[i] = 0;
            }
            System.out.println(str);

        } while (indexList[0] != '*');

    }

    public int getShellFreagmentSize(){
        return shellFragmentsList[0].values.length;
    }
}
