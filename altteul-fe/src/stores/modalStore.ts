// src/stores/modalStore.ts
import { create } from "zustand";
import { ModalInfo } from "types/modalTypes";

// modalStore.ts 수정
interface ModalState {
  currentModal: string | null;
  modalInfo: ModalInfo | null;
  profileUpdated: boolean; // 추가
  openModal: (modalName: string, info?: ModalInfo) => void;
  closeModal: () => void;
  isOpen: (modalName: string) => boolean;
  getModalInfo: () => ModalInfo | null;
  setProfileUpdated: (updated: boolean) => void; // 추가
}

const useModalStore = create<ModalState>((set, get) => ({
  currentModal: null,
  modalInfo: null,
  profileUpdated: false, // 추가
  openModal: (modalName, info = null) =>
    set({
      currentModal: modalName,
      modalInfo: info,
    }),
  closeModal: () =>
    set({
      currentModal: null,
      modalInfo: null,
    }),
  isOpen: (modalName) => get().currentModal === modalName,
  getModalInfo: () => get().modalInfo,
  setProfileUpdated: (updated) => set({ profileUpdated: updated }), // 추가
}));

export default useModalStore;
