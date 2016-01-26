module api.form.inputtype.text.htmlarea {

    import Option = api.ui.selector.Option;

    export class ImageCroppingOptions {

        static SQUARE: ImageCroppingOption = new ImageCroppingOption("square", 1, 1);

        static REGULAR: ImageCroppingOption = new ImageCroppingOption("regular", 4, 3);

        static WIDESCREEN: ImageCroppingOption = new ImageCroppingOption("widescreen", 16, 9);

        static CINEMA: ImageCroppingOption = new ImageCroppingOption("cinema", 21, 9);

        static PORTRAIT: ImageCroppingOption = new ImageCroppingOption("portrait", 3, 4);

        static TALL: ImageCroppingOption = new ImageCroppingOption("tall", 9, 16);

        static SKYSCRAPER: ImageCroppingOption = new ImageCroppingOption("skyscraper", 9, 21);

        constructor() {

        }

        getOptions(): Option<ImageCroppingOption>[] {

            var options: Option<ImageCroppingOption>[] = [];

            this.getCroppingOptions().forEach((imageCroppingOption: ImageCroppingOption) => {
                var option = {
                    value: imageCroppingOption.getName(),
                    displayValue: imageCroppingOption
                };

                options.push(option);
            })

            return options;
        }

        getCroppingOptions(): ImageCroppingOption[] {
            return [ImageCroppingOptions.SQUARE, ImageCroppingOptions.REGULAR, ImageCroppingOptions.WIDESCREEN, ImageCroppingOptions.CINEMA,
                ImageCroppingOptions.PORTRAIT, ImageCroppingOptions.TALL, ImageCroppingOptions.SKYSCRAPER];
        }

    }
}