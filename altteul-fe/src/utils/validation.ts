export interface ValidationErrors {
  username: string;
  password: string;
  confirmPassword: string;
  nickname: string;
  mainLang: string;
  profileImg: string;
}

export interface SignUpFormData {
  username: string;
  password: string;
  confirmPassword: string;
  nickname: string;
  mainLang: string;
  profileImg: File | null;
}

// 유효성 검사
export const validateSignUpForm = (
  form: SignUpFormData
): { isValid: boolean; errors: ValidationErrors } => {
  let errors: ValidationErrors = {
    username: "",
    password: "",
    confirmPassword: "",
    nickname: "",
    mainLang: "",
    profileImg: "",
  };
  let isValid = true;

  // 아이디 유효성 검사
  if (!/^[a-zA-Z0-9]{5,8}$/.test(form.username)) {
    errors.username =
      "아이디는 영문과 숫자의 조합으로 5자 이상, 8자 이하여야 합니다.";
    isValid = false;
  }

  // 비밀번호 유효성 검사
  if (
    form.password.length < 8 ||
    !/\d/.test(form.password) ||
    !/[a-zA-Z]/.test(form.password)
  ) {
    errors.password =
      "비밀번호는 영문과 숫자를 포함하여 8자 이상이어야 합니다.";
    isValid = false;
  }

  // 닉네임 유효성 검사
  // 공백포함 안됨

  if (form.nickname.trim().length === 0) {
    errors.nickname = "닉네임을 입력해주세요.";
    isValid = false;
  }

  if (form.nickname.length < 2 || form.nickname.length > 8) {
    errors.nickname = "닉네임은 2자 이상 8자 이하로 설정해야 합니다.";
    isValid = false;
  }

  if (form.profileImg) {
    const allowedExtensions = ["png", "jpg", "jpeg"];
    const fileExtension = form.profileImg.name.split(".").pop()?.toLowerCase();

    if (!fileExtension || !allowedExtensions.includes(fileExtension)) {
      errors.profileImg = "png, jpg, jpeg 파일만 업로드 가능합니다.";
      isValid = false;
    }
  }

  return { isValid, errors };
};
