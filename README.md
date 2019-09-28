# P2P-Java

Client 

```java AppClient 127.0.0.1 client01 127.0.0.1 "./share" ```

Server

```  rm -rf *.class && javac *.java && java AppServer 127.0.0.1 ```

ou

```  java AppServer 127.0.0.1 ```

## Requisitos

- Os peers devem se registrar no servidor para poderem realizar a troca de
informações.
- Durante o registro, um peer informa os recursos disponíveis (use um
diretório com alguns arquivos, e calcule a hash de cada um, a ser passada
ao servidor). Para cada arquivo, forneça ao servidor uma string ou o nome
do arquivo e sua hash.
- O servidor associa cada recurso em uma lista, juntamente com o IP do
peer onde está o recurso.
- Os peers podem solicitar uma lista de recursos (nomes e hashes) ou um
recurso específico ao servidor.
- Ao solicitar um recurso ao servidor, o peer recebe a informação sobre sua
localização (outro peer) e deve então realizar essa comunicação
diretamente com o mesmo.
- O servidor é responsável por manter a estrutura da rede de overlay. Para
isso os peers devem realizar solicitações periódicas ao servidor (a cada 5
segundos). Caso um peer não envie 2 solicitações seguidas a um servidor,
o mesmo é removido da lista.
