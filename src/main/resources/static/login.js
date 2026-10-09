const form = document.querySelector('#login-form');
const message = document.querySelector('#login-message');

fetch('/api/auth/csrf')
  .then((response) => {
    if (!response.ok) throw new Error('Güvenli giriş anahtarı alınamadı. Sayfayı yenileyin.');
    return response.json();
  })
  .then(({ token }) => { form.elements._csrf.value = token; })
  .catch((error) => {
    message.textContent = error.message;
    message.classList.remove('hidden');
  });

const params = new URLSearchParams(location.search);
if (params.has('error')) {
  message.textContent = 'E-posta veya parola hatalı ya da hesabınız devre dışı.';
  message.classList.remove('hidden');
} else if (params.has('logout')) {
  message.textContent = 'Oturumunuz güvenli biçimde kapatıldı.';
  message.classList.remove('hidden');
} else if (params.has('expired')) {
  message.textContent = 'Oturumunuz sona erdi. Devam etmek için tekrar giriş yapın.';
  message.classList.remove('hidden');
}
