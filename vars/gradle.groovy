def call(stages)
{
    echo 'Stages a ejecutar :' + stages
    def mapa = [100 : 'Continue', 200 : 'OK', 400 : 'badRequest']
    echo 'valor => ' + mapa.size()
}
return this;