let clienteWS = null;
let suscripcionGeneral = null;
let suscripcionArea = null;

function conectar() {
  const area = document.getElementById('areaSelect').value;
  if (!area) {
    alert('Por favor seleccione un área para conectar.');
    return;
  }

  const socket = new SockJS('http://localhost:8080/ws');
  clienteWS = Stomp.over(socket);

  clienteWS.connect({}, (frame) => {
    agregarNotificacion('Conectado como administrador del área: ' + area.toUpperCase());

    // Canal general: todas las solicitudes
    suscripcionGeneral = clienteWS.subscribe('/notificaciones/general', (msg) => {
      agregarNotificacion('NOTIFICACIÓN GENERAL:\n' + msg.body);
    });

    // Canal específico del área
    suscripcionArea = clienteWS.subscribe('/notificaciones/' + area, (msg) => {
      agregarNotificacion('NOTIFICACIÓN ÁREA ' + area.toUpperCase() + ':\n' + msg.body);
    });

    setConectado(true);
  }, (error) => {
    agregarNotificacion('Error al conectar: ' + error);
  });
}

function desconectar() {
  if (clienteWS !== null) {
    if (suscripcionGeneral) suscripcionGeneral.unsubscribe();
    if (suscripcionArea) suscripcionArea.unsubscribe();

    clienteWS.disconnect(() => {
      agregarNotificacion('Desconectado');
      setConectado(false);
    });
  }
  clienteWS = null;
}

function agregarNotificacion(mensaje) {
  const div = document.getElementById('notificaciones');
  const p = document.createElement('p');
  p.textContent = mensaje;
  div.appendChild(p);
  div.scrollTop = div.scrollHeight;
}

function setConectado(conectado) {
  document.getElementById('btnConectar').disabled = conectado;
  document.getElementById('btnDesconectar').disabled = !conectado;
  document.getElementById('areaSelect').disabled = conectado;
}
