document.addEventListener('DOMContentLoaded', () => {
  const walletCards = document.querySelectorAll('.wallet-item');
  walletCards.forEach((card) => {
    card.addEventListener('click', () => {
      card.classList.toggle('is-selected');
    });
  });
});
