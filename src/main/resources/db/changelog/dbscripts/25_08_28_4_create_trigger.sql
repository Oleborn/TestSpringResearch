-- Создаём (или переопределяем, если уже существует) функцию normalize_email
CREATE OR REPLACE FUNCTION normalize_email()
-- Функция возвращает "триггер" (то есть, срабатывает в контексте INSERT/UPDATE)
RETURNS TRIGGER AS $$
BEGIN
    -- Каждая строка в триггере доступна как NEW (новое значение) и OLD (старое значение).
    -- Здесь мы берём поле mail из вставляемой/обновляемой записи
    -- и приводим его к нижнему регистру.
    NEW.mail := LOWER(NEW.mail);

    -- Обязательно возвращаем NEW, чтобы операция вставки/обновления продолжилась
RETURN NEW;
END;
$$ LANGUAGE plpgsql;  -- Указываем, что функция написана на PL/pgSQL


-- Создаём триггер для таблицы user_app
CREATE TRIGGER trg_normalize_email_user_app
-- BEFORE INSERT OR UPDATE → триггер срабатывает перед вставкой или обновлением
    BEFORE INSERT OR UPDATE ON user_app
-- FOR EACH ROW → для каждой строки (а не один раз на весь батч операции)
                         FOR EACH ROW
-- EXECUTE FUNCTION → вызываем ранее созданную функцию normalize_email()
                         EXECUTE FUNCTION normalize_email();

-- Создаём аналогичный триггер для таблицы auth_user
CREATE TRIGGER trg_normalize_email_auth_user
    BEFORE INSERT OR UPDATE ON auth_user
                         FOR EACH ROW
                         EXECUTE FUNCTION normalize_email();