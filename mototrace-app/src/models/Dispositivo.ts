export interface Dispositivo {
    id: number;
    codigo: string;
    nome: string;
    descricao: string;
    cor: string
    bloqueado: boolean
    notificacaoAtiva: boolean
}
